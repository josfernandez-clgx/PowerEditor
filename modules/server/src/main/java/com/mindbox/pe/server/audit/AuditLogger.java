package com.mindbox.pe.server.audit;

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import com.mindbox.pe.model.Auditable;
import com.mindbox.pe.model.DateSynonym;
import com.mindbox.pe.model.GenericEntity;
import com.mindbox.pe.model.GuidelineContext;
import com.mindbox.pe.model.grid.AbstractGrid;
import com.mindbox.pe.model.template.AbstractTemplateColumn;
import com.mindbox.pe.model.template.GridTemplate;
import com.mindbox.pe.server.audit.command.AuditCommandInvoker;
import com.mindbox.pe.server.audit.command.CloneEntityAuditCommand;
import com.mindbox.pe.server.audit.command.CloneTemplateAuditCommand;
import com.mindbox.pe.server.audit.command.DefaultAuditCommandFactoryFactory;
import com.mindbox.pe.server.audit.command.DeleteActivationsAuditCommand;
import com.mindbox.pe.server.audit.command.DeleteGuidelineTemplateAuditCommand;
import com.mindbox.pe.server.audit.command.NewActivationAuditCommand;
import com.mindbox.pe.server.audit.command.NewTemplateWithCutoverAuditCommand;
import com.mindbox.pe.server.audit.command.SimpleEventAuditCommand;
import com.mindbox.pe.server.audit.command.ThreadedAuditCommandInvoker;
import com.mindbox.pe.server.audit.command.UpdateActivationAuditCommand;
import com.mindbox.pe.server.audit.command.UpdateMultiActivationContextAuditCommand;
import com.mindbox.pe.server.audit.event.AuditFailedEvent;
import com.mindbox.pe.server.audit.event.AuditFailedEventListener;
import com.mindbox.pe.server.audit.event.AuditFailedEventSupport;
import com.mindbox.pe.server.config.ConfigurationManager;
import com.mindbox.pe.server.spi.audit.AuditEventType;

/**
 * Audit Log manager.
 * Keep this stateless.
 * @author Gene Kim
 * @author MindBox, Inc
 * @since PowerEditor 1.10.0
 */
public class AuditLogger implements AuditFailedEventSupport {

	private static AuditLogger instance = null;

	public static synchronized AuditLogger getInstance() {
		if (instance == null) instance = new AuditLogger();
		return instance;
	}

	private final Logger logger;
	private final AuditStorage auditStorage;
	private final AuditCommandInvoker auditCommandInvoker;
	private final List<AuditFailedEventListener> auditErrorEvenListenerList;

	private AuditLogger() {
		this.logger = Logger.getLogger(getClass());
		boolean logOn = ConfigurationManager.getInstance().getServerConfigHelper().auditAll();
		if (!logOn) {
			logger.warn("Audit turned off; no audit events will be logged!!!");
		}

		this.auditCommandInvoker = new ThreadedAuditCommandInvoker(!logOn);
		this.auditStorage = new DefaultAuditStorage();

		auditErrorEvenListenerList = new ArrayList<AuditFailedEventListener>();
		auditErrorEvenListenerList.add(new AuditFailedEventListener() {
			public void auditFailed(AuditFailedEvent auditErrorEvent) {
				logger.error(auditErrorEvent.getMessage(), auditErrorEvent.getException());
			}
		});
	}

	////////////// audit failed event support /////////////////////////////////

	public void addAuditFailedEventListener(AuditFailedEventListener listener) {
		synchronized (auditErrorEvenListenerList) {
			auditErrorEvenListenerList.add(listener);
		}
	}

	private void fireAuditFailed(AuditFailedEvent auditErrorEvent) {
		synchronized (auditErrorEvenListenerList) {
			for (Iterator<AuditFailedEventListener> iter = auditErrorEvenListenerList.iterator(); iter.hasNext();) {
				AuditFailedEventListener element = iter.next();
				element.auditFailed(auditErrorEvent);
			}
		}
	}

	public void fireAuditFailed(String message, Exception exception) {
		fireAuditFailed(new AuditFailedEvent(message, exception));
	}

	public void logCloneEntity(GenericEntity newEntity, GenericEntity sourceEntity, boolean copyPolicies, String userID) {
		if (newEntity == null) throw new NullPointerException("newEntity cannot be null");
		if (sourceEntity == null) throw new NullPointerException("sourceEntity cannot be null");
		if (userID == null) throw new NullPointerException("userID cannot be null");
		auditCommandInvoker.execute(new CloneEntityAuditCommand((GenericEntity) newEntity.deepCopy(), sourceEntity, copyPolicies, new Date(), userID), auditStorage, this);
	}

	////////////////// specific audit log methods /////////////////////////////

	public void logCloneTemplate(GridTemplate templateToSave, int sourceTemplateID, String userID) {
		if (templateToSave == null) throw new NullPointerException("templateToSave cannot be null");
		if (userID == null) throw new NullPointerException("userID cannot be null");
		auditCommandInvoker.execute(CloneTemplateAuditCommand.getInstance(sourceTemplateID, (GridTemplate) templateToSave.deepCopy(), new Date(), userID), auditStorage, this);
	}

	public void logDelete(Auditable auditable, String userID) {
		if (auditable == null) throw new NullPointerException("auditable cannot be null");
		if (userID == null) throw new NullPointerException("userID cannot be null");
		auditCommandInvoker.execute(DefaultAuditCommandFactoryFactory.getAuditCommandFactory(auditable.getClass()).getDeleteInstance(auditable, new Date(), userID), auditStorage, this);
	}

	public void logDeleteGrids(List<? extends AbstractGrid<?>> deletedGridList, String userID) {
		if (userID == null) {
			throw new NullPointerException("userID cannot be null");
		}
		if (deletedGridList != null && !deletedGridList.isEmpty()) {

			auditCommandInvoker.execute(new DeleteActivationsAuditCommand(deletedGridList, new Date(), userID), auditStorage, this);
		}
	}

	public void logDeleteTemplate(GridTemplate template, boolean removePolicies, String userID) {
		if (template == null) throw new NullPointerException("template cannot be null");
		if (userID == null) throw new NullPointerException("userID cannot be null");
		auditCommandInvoker.execute(new DeleteGuidelineTemplateAuditCommand(template, removePolicies, new Date(), userID), auditStorage, this);
	}

	public void logDeployCompleted(final String description, final String userID) {
		if (userID == null) {
			throw new NullPointerException("userID cannot be null");
		}
		auditCommandInvoker.execute(new SimpleEventAuditCommand(AuditEventType.DEPLOY_COMPLETED, new Date(), userID, description), auditStorage, this);
	}

	public void logDeployStarted(final String description, final String userID) {
		if (userID == null) {
			throw new NullPointerException("userID cannot be null");
		}
		auditCommandInvoker.execute(new SimpleEventAuditCommand(AuditEventType.DEPLOY_STARTED, new Date(), userID, description), auditStorage, this);
	}

	public void logImport(Auditable auditable, String userID) {
		if (auditable == null) throw new NullPointerException("auditable cannot be null");
		if (userID == null) throw new NullPointerException("userID cannot be null");
		auditCommandInvoker.execute(DefaultAuditCommandFactoryFactory.getAuditCommandFactory(auditable.getClass()).getImportInstance(auditable.deepCopy(), new Date(), userID), auditStorage, this);
	}

	public void logLogIn(String userID) {
		if (userID == null) {
			throw new NullPointerException("userID cannot be null");
		}
		auditCommandInvoker.execute(new SimpleEventAuditCommand(AuditEventType.LOGON, new Date(), userID, String.format("User %s logged in", userID)), auditStorage, this);
	}

	public void logLogOff(String userID) {
		if (userID == null) {
			throw new NullPointerException("userID cannot be null");
		}
		auditCommandInvoker.execute(new SimpleEventAuditCommand(AuditEventType.LOGOFF, new Date(), userID, String.format("User %s logged off", userID)), auditStorage, this);
	}

	public void logNew(Auditable auditable, String userID) {
		if (auditable == null) {
			throw new NullPointerException("auditable cannot be null");
		}
		if (userID == null) {
			throw new NullPointerException("userID cannot be null");
		}
		auditCommandInvoker.execute(DefaultAuditCommandFactoryFactory.getAuditCommandFactory(auditable.getClass()).getNewInstance(auditable.deepCopy(), new Date(), userID), auditStorage, this);
	}

	public void logNewGrid(AbstractGrid<?> newGrid, String userID) {
		if (newGrid == null) throw new NullPointerException("newGrid cannot be null");
		if (userID == null) throw new NullPointerException("userID cannot be null");
		auditCommandInvoker.execute(new NewActivationAuditCommand((AbstractGrid<?>) newGrid.deepCopy(), new Date(), userID), auditStorage, this);
	}

	public void logNewTemplateWithCutover(GridTemplate templateToSave, int sourceTemplateID, DateSynonym cutoverDate, String userID) {
		if (templateToSave == null) throw new NullPointerException("templateToSave cannot be null");
		if (cutoverDate == null) throw new NullPointerException("cutoverDate cannot be null");
		if (userID == null) throw new NullPointerException("userID cannot be null");
		auditCommandInvoker.execute(NewTemplateWithCutoverAuditCommand.getInstance(sourceTemplateID, (GridTemplate) templateToSave.deepCopy(), cutoverDate, new Date(), userID), auditStorage, this);
	}

	public void logServerShutdown() {
		auditCommandInvoker.execute(new SimpleEventAuditCommand(AuditEventType.SERVER_SHUTDOWN, new Date(), null, "PowerEditor Shutdown"), auditStorage, this);
	}

	public void logServerStartup() {
		auditCommandInvoker.execute(new SimpleEventAuditCommand(AuditEventType.SERVER_STARTUP, new Date(), null, "PowerEditor Started"), auditStorage, this);
	}

	public void logUpdate(Auditable newAuditable, Auditable oldAuditable, String userID) {
		if (newAuditable == null) throw new NullPointerException("newAuditable cannot be null");
		if (oldAuditable == null) throw new NullPointerException("oldAuditable cannot be null");
		if (userID == null) throw new NullPointerException("userID cannot be null");
		auditCommandInvoker.execute(
				DefaultAuditCommandFactoryFactory.getAuditCommandFactory(oldAuditable.getClass()).getUpdateInstance(newAuditable.deepCopy(), oldAuditable.deepCopy(), new Date(), userID),
				auditStorage,
				this);
	}

	public <T extends AbstractGrid<?>> void logUpdateContext(Map<T, GuidelineContext[]> gridOldContextMap, GuidelineContext[] newContexts, String userID) {
		if (userID == null) {
			throw new NullPointerException("userID cannot be null");
		}
		if (gridOldContextMap != null && !gridOldContextMap.isEmpty()) {
			auditCommandInvoker.execute(new UpdateMultiActivationContextAuditCommand<T>(gridOldContextMap, newContexts, new Date(), userID), auditStorage, this);
		}
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public <C extends AbstractTemplateColumn> void logUpdateGrid(AbstractGrid<C> newGrid, AbstractGrid<C> oldGrid, String userID) {
		if (newGrid == null) throw new NullPointerException("newGrid cannot be null");
		if (oldGrid == null) throw new NullPointerException("oldGrid cannot be null");
		if (userID == null) throw new NullPointerException("userID cannot be null");
		auditCommandInvoker.execute(new UpdateActivationAuditCommand((AbstractGrid<C>) newGrid.deepCopy(), (AbstractGrid<C>) oldGrid.deepCopy(), new Date(), userID), auditStorage, this);
	}

	public void removeAuditFailedEventListener(AuditFailedEventListener listener) {
		synchronized (auditErrorEvenListenerList) {
			auditErrorEvenListenerList.remove(listener);
		}
	}
}
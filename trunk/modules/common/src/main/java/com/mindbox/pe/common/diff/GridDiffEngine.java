package com.mindbox.pe.common.diff;

import com.mindbox.pe.model.grid.AbstractGrid;

public interface GridDiffEngine {

	<G extends AbstractGrid<?>> GridDiffResult diff(G grid1, G grid2);
}

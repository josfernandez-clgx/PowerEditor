package com.mindbox.pe.communication;


/**
 * @author Vineet Khosla
 * @since 5.0.0
 */
public class ExportRequestToServerResponse extends ResponseComm {

	private static final long serialVersionUID = 1125407814100133044L;

	private int mGenerateRunId;
	
    public ExportRequestToServerResponse(int i)
    {
        setGenerateRunId(i);
    }

    private void setGenerateRunId(int i)
    {
        mGenerateRunId = i;
    }

    public int getGenerateRunId()
    {
        return mGenerateRunId;
    }

    
}

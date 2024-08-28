package com.ericsson.eniq.events.ui.client.datatype;

/**
 * -----------------------------------------------------------------------
 * Copyright (C) 2013 LM Ericsson Limited.  All rights reserved.
 * -----------------------------------------------------------------------
 */
public enum GridCellColorBlockType {
    
    SUCCESS_COLOR_BLOCK_CSS("colorBlockSuccess"),
    REJECT_COLOR_BLOCK_CSS("colorBlockReject"),
    DROPPED_COLOR_BLOCK_CSS("colorBlockDropped"),
    BLOCKED_COLOR_BLOCK_CSS("colorBlockBlocked"),
    ERROR_COLOR_BLOCK_CSS("colorBlockError"),
    ABORT_COLOR_BLOCK_CSS("colorBlockAbort"),
    IGNORE_COLOR_BLOCK_CSS("colorBlockIgnore");
    
    private String type;
    
    GridCellColorBlockType(final String type){
        this.type = type;
    }
    
    public static GridCellColorBlockType getColorBlockFromCell(final String cellValue){
        GridCellColorBlockType colorBlockType = SUCCESS_COLOR_BLOCK_CSS;
        if(cellValue.equalsIgnoreCase("REJECT")){
            colorBlockType = REJECT_COLOR_BLOCK_CSS;
        }else if(cellValue.equalsIgnoreCase("DROPPED")){
            colorBlockType = DROPPED_COLOR_BLOCK_CSS;
        }else if(cellValue.equalsIgnoreCase("BLOCKED")){
            colorBlockType = BLOCKED_COLOR_BLOCK_CSS;
        }else if(cellValue.equalsIgnoreCase("ERROR")){
            colorBlockType = ERROR_COLOR_BLOCK_CSS;
        }else if(cellValue.equalsIgnoreCase("ABORT")){
            colorBlockType = ABORT_COLOR_BLOCK_CSS;
        }else if(cellValue.equalsIgnoreCase("IGNORE")){
            colorBlockType = IGNORE_COLOR_BLOCK_CSS;
        }
        return colorBlockType;
    }


    @Override
    public String toString() {
        return type;
    }
}

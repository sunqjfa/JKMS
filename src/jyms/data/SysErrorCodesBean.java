/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jyms.data;

import java.io.Serializable;

/**
 *
 * @author John
 */

public class SysErrorCodesBean {

    private static final long serialVersionUID = 1L;

    private Integer ErrorCode;
    private String ErrorType;
    private String ErrorClass;
    private String ErrorDesc;
    private String Remarks = "";
    
    private static final String sFileName = "--->>SysErrorCodesBean.java";

    public SysErrorCodesBean() {
    }

    public SysErrorCodesBean(Integer errorcode) {
        this.ErrorCode = errorcode;
    }

    public SysErrorCodesBean(Integer errorcode, String errortype, String errorclass,String errordesc, String remarks) {
        this.ErrorCode = errorcode;
        this.ErrorType = errortype;
        this.ErrorClass = errorclass;
        this.ErrorDesc = errordesc;
        this.Remarks = remarks;
    }

    public Integer getErrorcode() {
        return ErrorCode;
    }

    public void setErrorcode(Integer errorcode) {
        this.ErrorCode = errorcode;
    }

    public String getErrortype() {
        return ErrorType;
    }

    public void setErrortype(String errortype) {
        this.ErrorType = errortype;
    }

    public String getErrorclass() {
        return ErrorClass;
    }

    public void setErrorclass(String errorclass) {
        this.ErrorClass = errorclass;
    }

    public String getErrordesc() {
        return ErrorDesc;
    }

    public void setErrordesc(String errordesc) {
        this.ErrorDesc = errordesc;
    }

    public String getRemarks() {
        return Remarks;
    }

    public void setRemarks(String remarks) {
        this.Remarks = remarks;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (ErrorCode != null ? ErrorCode.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof SysErrorCodesBean)) {
            return false;
        }
        SysErrorCodesBean other = (SysErrorCodesBean) object;
        if ((this.ErrorCode == null && other.ErrorCode != null) || (this.ErrorCode != null && !this.ErrorCode.equals(other.ErrorCode))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "jyms.data.Syserrorcodes[ errorcode=" + ErrorCode + " ]";
    }
    /**
        * 函数:      getErrorMsg
        * 函数描述:  获得SDK的错误描述
        * @param ErrorCode2 错误代码
        * @param FileName     调用该函数的文件名
        * @return String 错误描述 
    */
    public static String getErrorMsg(int ErrorCode2, String FileName){
        if (ErrorCode2 < 0) return "";
        String sqlQuery = "SELECT errordesc  FROM Syserrorcodes where ErrorCode = " + ErrorCode2 + "";
        String ErrorDesc2 = InitDB.getInitDB(FileName + sFileName).executeQueryOneCol(sqlQuery);
        
        return ErrorDesc2 == null?"":ErrorDesc2;
    }
}

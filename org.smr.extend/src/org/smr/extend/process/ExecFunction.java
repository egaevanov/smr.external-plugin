package org.smr.extend.process;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.logging.Level;

import org.compiere.process.ProcessInfoParameter;
import org.compiere.process.SvrProcess;
import org.compiere.util.DB;

public class ExecFunction extends SvrProcess{

	private int p_AD_Process_ID = 0;
	private String p_Function_Name = "";
	private int p_ParameterCount = 0;

	private ProcessInfoParameter[] para = null;
	private HashMap<String, String> ParaMap= new HashMap<>();
	
	@Override
	protected void prepare() {
		para = getParameter();
		p_AD_Process_ID = getProcessInfo().getAD_Process_ID();
		
		StringBuilder SQLGetPara = new StringBuilder();
		
		SQLGetPara.append("SELECT ColumnName,AD_Reference_ID,SeqNo ");
		SQLGetPara.append(" FROM AD_Process_Para");
		SQLGetPara.append(" WHERE AD_Process_ID = ?");
		SQLGetPara.append(" Order By SeqNo ASC");
		
		PreparedStatement pstmtPara = null;
     	ResultSet rsPara = null;
			try {
				pstmtPara = DB.prepareStatement(SQLGetPara.toString(), null);
				pstmtPara.setInt(1, p_AD_Process_ID);	
			
				rsPara = pstmtPara.executeQuery();
				while (rsPara.next()) {
					
					String ColumnName = rsPara.getString(1);
					Integer AD_Reference_ID = rsPara.getInt(2);
					String DataType = "";
					
					if(AD_Reference_ID == 11 || AD_Reference_ID == 12 || AD_Reference_ID == 13 || AD_Reference_ID == 18
							|| AD_Reference_ID == 19 || AD_Reference_ID == 21 || AD_Reference_ID == 22 || AD_Reference_ID == 25
							|| AD_Reference_ID == 26 || AD_Reference_ID == 29 || AD_Reference_ID == 30 || AD_Reference_ID == 31
							|| AD_Reference_ID == 35 || AD_Reference_ID == 37 ) {
						DataType = "Integer";
					}else if(AD_Reference_ID == 10 || AD_Reference_ID == 14 || AD_Reference_ID == 17 || AD_Reference_ID == 20 
							|| AD_Reference_ID == 36 || AD_Reference_ID == 38 || AD_Reference_ID == 40 || AD_Reference_ID == 200012) {
						DataType = "String";
					}else if(AD_Reference_ID == 15 || AD_Reference_ID == 16 || AD_Reference_ID == 24) {
						DataType = "Timestamp";
					}
						
					ParaMap.put(ColumnName, DataType);
					
				}

			} catch (SQLException err) {
				log.log(Level.SEVERE, SQLGetPara.toString(), err);
			} finally {
				DB.close(rsPara, pstmtPara);
				rsPara = null;
				pstmtPara = null;
			}
		
		
		for (int i = 0; i < para.length; i++){
			String name = para[i].getParameterName();
			if (para[i].getParameter() == null) {
			;	
			}else if(name.equals("Function_Name")) {
				p_Function_Name = (String)para[i].getParameterAsString();
			}
	
		}
		
		
		p_ParameterCount = para.length -1;
		
		
	}
	@Override
	protected String doIt() throws Exception {
			
		
		StringBuilder SQLParamFunctionClause = new StringBuilder();
		StringBuilder ParamMark = new StringBuilder();

		String PreFix = "(";
		String SufFix = ")";
		
		if(p_ParameterCount == 0) {
			SQLParamFunctionClause.append(PreFix+SufFix);
		}else if(p_ParameterCount > 0) {
			for (int i = 1 ; i <= p_ParameterCount ; i++) {
				
				if(i < p_ParameterCount) {
					ParamMark.append("?,");
				}else if(i == p_ParameterCount){
					ParamMark.append("?");
				}		
			}
			
			SQLParamFunctionClause.append(PreFix+ParamMark+SufFix);

		}
		
		
		StringBuilder SQLExecuteFunction = new StringBuilder();
		SQLExecuteFunction.append("SELECT "+p_Function_Name+SQLParamFunctionClause);
		
		System.out.println(SQLExecuteFunction.toString());
		
		PreparedStatement pstmt = null;
     	ResultSet rs = null;
			try {
				pstmt = DB.prepareStatement(SQLExecuteFunction.toString(), null);
				for(int n = 1; n < para.length ; n++) {
					String name = para[n].getParameterName();

					String DataType = ParaMap.get(name);
					if(DataType == "String") {
						pstmt.setString(n,para[n].getParameterAsString());
						System.out.println("Param "+n+" : "+ para[n].getParameterAsString());
					}else if(DataType == "Integer") {
						pstmt.setInt(n,para[n].getParameterAsInt());						
						System.out.println("Param "+n +" : "+ para[n].getParameterAsInt());
					}else if(DataType == "BigDecimal") {
						pstmt.setBigDecimal(n,para[n].getParameterAsBigDecimal());
						System.out.println("Param "+n +" : "+ para[n].getParameterAsBigDecimal());

					}else if(DataType == "Timestamp") {
						pstmt.setTimestamp(n,para[n].getParameterAsTimestamp());
						System.out.println("Param "+n +" : "+ para[n].getParameterAsTimestamp());

					}
					
				}
			
				rs = pstmt.executeQuery();
				while (rs.next()) {
					
				}

			} catch (SQLException err) {
				log.log(Level.SEVERE, SQLExecuteFunction.toString(), err);
			} finally {
				DB.close(rs, pstmt);
				rs = null;
				pstmt = null;
			}
		
		return "";
				
	}
	

}

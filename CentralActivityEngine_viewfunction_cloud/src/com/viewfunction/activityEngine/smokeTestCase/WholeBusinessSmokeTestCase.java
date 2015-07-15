package com.viewfunction.activityEngine.smokeTestCase;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

import javax.jcr.PropertyType;

import com.viewfunction.activityEngine.activityBureau.ActivitySpace;
import com.viewfunction.activityEngine.activityBureau.BusinessActivityDefinition;
import com.viewfunction.activityEngine.activityView.RoleQueue;
import com.viewfunction.activityEngine.activityView.Roster;
import com.viewfunction.activityEngine.activityView.common.DataFieldDefinition;
import com.viewfunction.activityEngine.exception.ActivityEngineActivityException;
import com.viewfunction.activityEngine.exception.ActivityEngineDataException;
import com.viewfunction.activityEngine.exception.ActivityEngineException;
import com.viewfunction.activityEngine.exception.ActivityEngineProcessException;
import com.viewfunction.activityEngine.exception.ActivityEngineRuntimeException;
import com.viewfunction.activityEngine.security.Participant;
import com.viewfunction.activityEngine.security.Role;
import com.viewfunction.activityEngine.util.factory.ActivityComponentFactory;

public class WholeBusinessSmokeTestCase {

	/**
	 * @param args
	 */
	public static void loadStructure() {
		try {
			ActivitySpace activitySpace=ActivityComponentFactory.createInitActivitySpace("viewfunction_inc");			
			Role testRole1=ActivityComponentFactory.createRole(activitySpace.getActivitySpaceName(), "Financial Department");
			testRole1.setDisplayName("财务部");
			testRole1.setDescription("Financial Department 财务部");
			activitySpace.addRole(testRole1);
			
			Participant participant1=ActivityComponentFactory.createParticipant("FinancialEmployeeA", Participant.PARTICIPANT_TYPE_USER,activitySpace.getActivitySpaceName());
			participant1.setDisplayName("财务部雇员A");
			activitySpace.addParticipant(participant1);
			Participant participant2=ActivityComponentFactory.createParticipant("FinancialEmployeeB", Participant.PARTICIPANT_TYPE_USER,activitySpace.getActivitySpaceName());
			participant2.setDisplayName("财务部雇员B");
			activitySpace.addParticipant(participant2);
			Participant participant3=ActivityComponentFactory.createParticipant("FinancialEmployeeC", Participant.PARTICIPANT_TYPE_USER,activitySpace.getActivitySpaceName());
			participant3.setDisplayName("财务部雇员C");
			activitySpace.addParticipant(participant3);				
			testRole1.addParticipant(participant1.getParticipantName());
			testRole1.addParticipant(participant2.getParticipantName());
			testRole1.addParticipant(participant3.getParticipantName());			
			
			Role testRole2=ActivityComponentFactory.createRole(activitySpace.getActivitySpaceName(), "Sales Department");
			testRole2.setDisplayName("销售部");
			testRole2.setDescription("Sales Department 销售部");
			activitySpace.addRole(testRole2);
			
			Participant participant4=ActivityComponentFactory.createParticipant("SalesEmployeeA", Participant.PARTICIPANT_TYPE_USER,activitySpace.getActivitySpaceName());
			participant4.setDisplayName("销售部雇员A");
			activitySpace.addParticipant(participant4);
			Participant participant5=ActivityComponentFactory.createParticipant("SalesEmployeeB", Participant.PARTICIPANT_TYPE_USER,activitySpace.getActivitySpaceName());
			participant5.setDisplayName("销售部雇员B");
			activitySpace.addParticipant(participant5);
			Participant participant6=ActivityComponentFactory.createParticipant("SalesEmployeeC", Participant.PARTICIPANT_TYPE_USER,activitySpace.getActivitySpaceName());
			participant6.setDisplayName("销售部雇员C");
			activitySpace.addParticipant(participant6);		
			testRole2.addParticipant(participant4.getParticipantName());
			testRole2.addParticipant(participant5.getParticipantName());
			testRole2.addParticipant(participant6.getParticipantName());			
			
			Role testRole3=ActivityComponentFactory.createRole(activitySpace.getActivitySpaceName(), "Manufacturing Department");
			testRole3.setDisplayName("制造部");
			testRole3.setDescription("Manufacturing Department 制造部");
			activitySpace.addRole(testRole3);
			
			Participant participant7=ActivityComponentFactory.createParticipant("ManufacturingEmployeeA", Participant.PARTICIPANT_TYPE_USER,activitySpace.getActivitySpaceName());
			participant7.setDisplayName("制造部雇员A");
			activitySpace.addParticipant(participant7);
			Participant participant8=ActivityComponentFactory.createParticipant("ManufacturingEmployeeB", Participant.PARTICIPANT_TYPE_USER,activitySpace.getActivitySpaceName());
			participant8.setDisplayName("制造部雇员B");
			activitySpace.addParticipant(participant8);
			Participant participant9=ActivityComponentFactory.createParticipant("ManufacturingEmployeeC", Participant.PARTICIPANT_TYPE_USER,activitySpace.getActivitySpaceName());
			participant9.setDisplayName("制造部雇员C");
			activitySpace.addParticipant(participant9);
			testRole3.addParticipant(participant7.getParticipantName());
			testRole3.addParticipant(participant8.getParticipantName());
			testRole3.addParticipant(participant9.getParticipantName());
			
			Role testRole4=ActivityComponentFactory.createRole(activitySpace.getActivitySpaceName(), "Quality Inspection Department");
			testRole4.setDisplayName("质检部");
			testRole4.setDescription("Quality Inspection Department 质检部");
			activitySpace.addRole(testRole4);
			
			Participant participant10=ActivityComponentFactory.createParticipant("QualityInspectionEmployeeA", Participant.PARTICIPANT_TYPE_USER,activitySpace.getActivitySpaceName());
			participant10.setDisplayName("质检部雇员A");
			activitySpace.addParticipant(participant10);
			Participant participant11=ActivityComponentFactory.createParticipant("QualityInspectionEmployeeB", Participant.PARTICIPANT_TYPE_USER,activitySpace.getActivitySpaceName());
			participant11.setDisplayName("质检部雇员B");
			activitySpace.addParticipant(participant11);
			Participant participant12=ActivityComponentFactory.createParticipant("QualityInspectionEmployeeC", Participant.PARTICIPANT_TYPE_USER,activitySpace.getActivitySpaceName());
			participant12.setDisplayName("质检部雇员C");
			activitySpace.addParticipant(participant12);
			testRole4.addParticipant(participant10.getParticipantName());
			testRole4.addParticipant(participant11.getParticipantName());
			testRole4.addParticipant(participant12.getParticipantName());
			
			Role testRole5=ActivityComponentFactory.createRole(activitySpace.getActivitySpaceName(), "Distribution Department");
			testRole5.setDisplayName("物流部");
			testRole5.setDescription("Distribution Department 物流部");
			activitySpace.addRole(testRole5);
			
			Participant participant13=ActivityComponentFactory.createParticipant("DistributionEmployeeA", Participant.PARTICIPANT_TYPE_USER,activitySpace.getActivitySpaceName());
			participant13.setDisplayName("物流部雇员A");
			activitySpace.addParticipant(participant13);			
			Participant participant14=ActivityComponentFactory.createParticipant("DistributionEmployeeB", Participant.PARTICIPANT_TYPE_USER,activitySpace.getActivitySpaceName());
			participant14.setDisplayName("物流部雇员B");
			activitySpace.addParticipant(participant14);			
			Participant participant15=ActivityComponentFactory.createParticipant("DistributionEmployeeC", Participant.PARTICIPANT_TYPE_USER,activitySpace.getActivitySpaceName());
			participant15.setDisplayName("物流部雇员C");
			activitySpace.addParticipant(participant15);
			testRole5.addParticipant(participant13.getParticipantName());
			testRole5.addParticipant(participant14.getParticipantName());
			testRole5.addParticipant(participant15.getParticipantName());
			
			Role testRole6=ActivityComponentFactory.createRole(activitySpace.getActivitySpaceName(), "Management Department");
			testRole6.setDisplayName("经理部");
			testRole6.setDescription("Management Department 经理部");
			activitySpace.addRole(testRole6);
			
			Participant participant16=ActivityComponentFactory.createParticipant("ManagerA", Participant.PARTICIPANT_TYPE_USER,activitySpace.getActivitySpaceName());
			participant16.setDisplayName("经理A");
			activitySpace.addParticipant(participant16);
			Participant participant17=ActivityComponentFactory.createParticipant("ManagerB", Participant.PARTICIPANT_TYPE_USER,activitySpace.getActivitySpaceName());
			participant17.setDisplayName("经理B");
			activitySpace.addParticipant(participant17);			
			testRole6.addParticipant(participant16.getParticipantName());
			testRole6.addParticipant(participant17.getParticipantName());			
			
			testRole1.addParticipant(participant16.getParticipantName());	
			testRole2.addParticipant(participant16.getParticipantName());
			testRole3.addParticipant(participant16.getParticipantName());			
			testRole4.addParticipant(participant17.getParticipantName());
			testRole5.addParticipant(participant17.getParticipantName());
			
			RoleQueue _RoleQueue1=ActivityComponentFactory.createRoleQueue("FinancialDepartmentQueue", activitySpace.getActivitySpaceName(), "财务部任务队列", "Financial DepartmentRoleQueue 财务部任务队列");
			activitySpace.addRoleQueue(_RoleQueue1);
			_RoleQueue1.addRole(testRole1.getRoleName());
			
			RoleQueue _RoleQueue2=ActivityComponentFactory.createRoleQueue("SalesDepartmentQueue", activitySpace.getActivitySpaceName(), "销售部任务队列", "Sales DepartmentRoleQueue 销售部任务队列");		
			activitySpace.addRoleQueue(_RoleQueue2);
			_RoleQueue2.addRole(testRole2.getRoleName());
			
			RoleQueue _RoleQueue3=ActivityComponentFactory.createRoleQueue("ManufacturingDepartmentQueue", activitySpace.getActivitySpaceName(), "制造部任务队列", "Manufacturing DepartmentRoleQueue 制造部任务队列");		
			activitySpace.addRoleQueue(_RoleQueue3);
			_RoleQueue3.addRole(testRole3.getRoleName());
			
			RoleQueue _RoleQueue4=ActivityComponentFactory.createRoleQueue("QualityInspectionDepartmentQueue", activitySpace.getActivitySpaceName(), "质检部任务队列", "Quality Inspection DepartmentRoleQueue 质检部任务队列");		
			activitySpace.addRoleQueue(_RoleQueue4);
			_RoleQueue4.addRole(testRole4.getRoleName());
			
			RoleQueue _RoleQueue5=ActivityComponentFactory.createRoleQueue("DistributionDepartmentQueue", activitySpace.getActivitySpaceName(), "物流部任务队列", "Distribution  DepartmentRoleQueue 物流部任务队列");		
			activitySpace.addRoleQueue(_RoleQueue5);
			_RoleQueue5.addRole(testRole5.getRoleName());
			
			RoleQueue _RoleQueue6=ActivityComponentFactory.createRoleQueue("ManagementDepartmentQueue", activitySpace.getActivitySpaceName(), "经理部任务队列", "Management  DepartmentRoleQueue 经理部任务队列");		
			activitySpace.addRoleQueue(_RoleQueue6);
			_RoleQueue6.addRole(testRole6.getRoleName());			
			
		} catch (ActivityEngineException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
	
	public static void deployProcess_1() throws FileNotFoundException, ActivityEngineDataException, ActivityEngineProcessException, ActivityEngineRuntimeException, ActivityEngineActivityException{
		//制造部财务报告
		ActivitySpace activitySpace=ActivityComponentFactory.getActivitySpace("viewfunction_inc");		
		
		DataFieldDefinition dfoperItem=ActivityComponentFactory.cteateDataFieldDefinition("operationItemName", PropertyType.STRING, false);
		dfoperItem.setDescription("Title of this operation");
		dfoperItem.setDisplayName("业务名称");
		dfoperItem.setMandatoryField(true);
		dfoperItem.setSystemField(false);
		
		
		DataFieldDefinition dfoperOnwer=ActivityComponentFactory.cteateDataFieldDefinition("operationOwner", PropertyType.STRING, false);
		dfoperOnwer.setDescription("who was in charge for this operation");
		dfoperOnwer.setDisplayName("业务负责人");
		dfoperOnwer.setMandatoryField(true);
		dfoperOnwer.setSystemField(false);
		
		DataFieldDefinition dfoperCustomer=ActivityComponentFactory.cteateDataFieldDefinition("operationCustomer", PropertyType.STRING, false);
		dfoperCustomer.setDescription("who was customer of this operation");
		dfoperCustomer.setDisplayName("业务客户");
		dfoperCustomer.setMandatoryField(true);
		dfoperCustomer.setSystemField(false);
		
		
		DataFieldDefinition dfoperCost=ActivityComponentFactory.cteateDataFieldDefinition("operationCost", PropertyType.DOUBLE, false);
		dfoperCost.setDescription("The final cost of this operation");
		dfoperCost.setDisplayName("业务最终花费");
		dfoperCost.setMandatoryField(true);
		dfoperCost.setSystemField(false);
		
		DataFieldDefinition dfoperIncome=ActivityComponentFactory.cteateDataFieldDefinition("operationIncome", PropertyType.DOUBLE, false);
		dfoperIncome.setDescription("The final income of this operation");
		dfoperIncome.setDisplayName("业务最终利润");
		dfoperIncome.setMandatoryField(true);
		dfoperIncome.setSystemField(false);
		
		
		DataFieldDefinition dfoperVerifyResult=ActivityComponentFactory.cteateDataFieldDefinition("operationVerfifyResult", PropertyType.BOOLEAN, false);
		dfoperVerifyResult.setDescription("the result of operation verify");
		dfoperVerifyResult.setDisplayName("业务花费评审结果");
		dfoperVerifyResult.setMandatoryField(true);
		dfoperVerifyResult.setSystemField(false);		
		
		DataFieldDefinition[] dfdArray=new DataFieldDefinition[6];
		dfdArray[0]=dfoperItem;
		dfdArray[1]=dfoperOnwer;
		dfdArray[2]=dfoperCustomer;		
		dfdArray[3]=dfoperCost;
		dfdArray[4]=dfoperIncome;
		dfdArray[5]=dfoperVerifyResult;
		
		String[] exposedSteps=new String[]{"writeReportTask","verifyReportTask"};
		
		BusinessActivityDefinition bsd=ActivityComponentFactory.createBusinessActivityDefinition("制造部财务报告", "BeaconManufactureLTD",exposedSteps);
		bsd.setActivityDataFields(dfdArray);		
		FileInputStream fileInputStream = new FileInputStream(new File("processDefine/制造部财务报告.bpmn20.xml"));
		bsd.setDefinitionResource(fileInputStream);		
		
		DataFieldDefinition[] step1edfdArray=new DataFieldDefinition[5];
		step1edfdArray[0]=dfoperItem;
		step1edfdArray[1]=dfoperOnwer;
		step1edfdArray[2]=dfoperCustomer;
		step1edfdArray[3]=dfoperCost;
		step1edfdArray[4]=dfoperIncome;		
		
		DataFieldDefinition[] step2edfdArray=new DataFieldDefinition[6];
		step2edfdArray[0]=dfoperItem;
		step2edfdArray[1]=dfoperOnwer;
		step2edfdArray[2]=dfoperCustomer;
		step2edfdArray[3]=dfoperCost;
		step2edfdArray[4]=dfoperIncome;	
		step2edfdArray[5]=dfoperVerifyResult;	
		
		bsd.setActivityStepExposedDataFields("writeReportTask", step1edfdArray);
		bsd.setActivityStepRelatedRole("writeReportTask", "Manufacturing Department");
		
		bsd.setActivityStepExposedDataFields("verifyReportTask", step2edfdArray);
		bsd.setActivityStepRelatedRole("verifyReportTask", "Financial Department");
		
		
		activitySpace.addBusinessActivityDefinition(bsd);
		
		
		
	}
	
	public static void deployProcess_2() throws FileNotFoundException, ActivityEngineDataException, ActivityEngineProcessException, ActivityEngineRuntimeException, ActivityEngineActivityException{
		//制造部采购审批
		ActivitySpace activitySpace=ActivityComponentFactory.getActivitySpace("viewfunction_inc");		
		
		DataFieldDefinition _PurchaseItemName=ActivityComponentFactory.cteateDataFieldDefinition("PurchaseItemName", PropertyType.STRING, false);
		_PurchaseItemName.setDescription("Purchase Item Name 采购物品名称");
		_PurchaseItemName.setDisplayName("采购物品名称");
		_PurchaseItemName.setMandatoryField(true);
		_PurchaseItemName.setSystemField(false);
		
		DataFieldDefinition _PurchaseItemAmount=ActivityComponentFactory.cteateDataFieldDefinition("PurchaseItemAmount", PropertyType.DOUBLE, false);
		_PurchaseItemAmount.setDescription("Purchase Item Amount 采购物品数量");
		_PurchaseItemAmount.setDisplayName("采购物品数量");
		_PurchaseItemAmount.setMandatoryField(true);
		_PurchaseItemAmount.setSystemField(false);
		
		DataFieldDefinition _PurchaseReason=ActivityComponentFactory.cteateDataFieldDefinition("PurchaseReason", PropertyType.STRING, false);
		_PurchaseReason.setDescription("PurchaseReason 采购理由");
		_PurchaseReason.setDisplayName("采购理由");
		_PurchaseReason.setMandatoryField(true);
		_PurchaseReason.setSystemField(false);
		
		DataFieldDefinition _PurchaseItemMoney=ActivityComponentFactory.cteateDataFieldDefinition("PurchaseItemMoney", PropertyType.DOUBLE, false);
		_PurchaseItemMoney.setDescription("PurchaseItemMoney 采购物品所需金额");
		_PurchaseItemMoney.setDisplayName("采购物品所需金额");
		_PurchaseItemMoney.setMandatoryField(true);
		_PurchaseItemMoney.setSystemField(false);
		
		/*
		DataFieldDefinition approvalResult=ActivityComponentFactory.cteateDataFieldDefinition("approvalResult", PropertyType.STRING, false);
		approvalResult.setDescription("approvalResult 审批结果");
		approvalResult.setDisplayName("审批结果");
		approvalResult.setMandatoryField(true);
		approvalResult.setSystemField(false);
		*/
		
		DataFieldDefinition[] dfdArray=new DataFieldDefinition[4];
		dfdArray[0]=_PurchaseItemName;
		dfdArray[1]=_PurchaseItemAmount;
		dfdArray[2]=_PurchaseReason;		
		dfdArray[3]=_PurchaseItemMoney;
		//dfdArray[4]=approvalResult;		
		
		String[] exposedSteps=new String[]{"submitPurchaseApply","VerifyPurchaseApply","ApprovePurchaseApply"};
		
		BusinessActivityDefinition bsd=ActivityComponentFactory.createBusinessActivityDefinition("制造部采购审批", "viewfunction_inc",exposedSteps);
		bsd.setActivityDataFields(dfdArray);		
		FileInputStream fileInputStream = new FileInputStream(new File("processDefine/制造部采购审批.bpmn20.xml"));
		bsd.setDefinitionResource(fileInputStream);		
		
		DataFieldDefinition[] step1edfdArray=new DataFieldDefinition[3];
		step1edfdArray[0]=_PurchaseItemName;
		step1edfdArray[1]=_PurchaseItemAmount;
		step1edfdArray[2]=_PurchaseReason;
		bsd.setActivityStepExposedDataFields("submitPurchaseApply", step1edfdArray);
		bsd.setActivityStepRelatedRole("submitPurchaseApply", "Manufacturing Department");			
		
		DataFieldDefinition[] step2edfdArray=new DataFieldDefinition[4];
		step2edfdArray[0]=_PurchaseItemName;
		step2edfdArray[1]=_PurchaseItemAmount;
		step2edfdArray[2]=_PurchaseReason;
		step2edfdArray[3]=_PurchaseItemMoney;		
		bsd.setActivityStepExposedDataFields("VerifyPurchaseApply", step2edfdArray);
		bsd.setActivityStepRelatedRole("VerifyPurchaseApply", "Financial Department");
		
		DataFieldDefinition[] step3edfdArray=new DataFieldDefinition[4];
		step3edfdArray[0]=_PurchaseItemName;
		step3edfdArray[1]=_PurchaseItemAmount;
		step3edfdArray[2]=_PurchaseReason;
		step3edfdArray[3]=_PurchaseItemMoney;		
		bsd.setActivityStepExposedDataFields("ApprovePurchaseApply", step3edfdArray);
		bsd.setActivityStepRelatedRole("ApprovePurchaseApply", "Management Department");		
		
		activitySpace.addBusinessActivityDefinition(bsd);		
	}
	
	public static void deployProcess_3() throws FileNotFoundException, ActivityEngineDataException, ActivityEngineProcessException, ActivityEngineRuntimeException, ActivityEngineActivityException{
		//业务出差报销
		ActivitySpace activitySpace=ActivityComponentFactory.getActivitySpace("viewfunction_inc");		
		
		DataFieldDefinition _BusinessTripTarget=ActivityComponentFactory.cteateDataFieldDefinition("BusinessTripTarget", PropertyType.STRING, false);
		_BusinessTripTarget.setDescription("BusinessTripTargete 出差地点");
		_BusinessTripTarget.setDisplayName("出差地点");
		_BusinessTripTarget.setMandatoryField(true);
		_BusinessTripTarget.setSystemField(false);
		
		DataFieldDefinition _BusinessTripDays=ActivityComponentFactory.cteateDataFieldDefinition("BusinessTripDays", PropertyType.DOUBLE, false);
		_BusinessTripDays.setDescription("BusinessTripDays 出差天数");
		_BusinessTripDays.setDisplayName("出差天数");
		_BusinessTripDays.setMandatoryField(true);
		_BusinessTripDays.setSystemField(false);
		
		DataFieldDefinition _BusinessTripSpent=ActivityComponentFactory.cteateDataFieldDefinition("BusinessTripSpent", PropertyType.DOUBLE, false);
		_BusinessTripSpent.setDescription("BusinessTripSpent 报销金额");
		_BusinessTripSpent.setDisplayName("报销金额");
		_BusinessTripSpent.setMandatoryField(true);
		_BusinessTripSpent.setSystemField(false);		
		
		DataFieldDefinition _BankAccount=ActivityComponentFactory.cteateDataFieldDefinition("BankAccount", PropertyType.STRING, false);
		_BankAccount.setDescription("BankAccount 报销银行账户");
		_BankAccount.setDisplayName("报销银行账户");
		_BankAccount.setMandatoryField(true);
		_BankAccount.setSystemField(false);		
		
		DataFieldDefinition[] dfdArray=new DataFieldDefinition[4];
		dfdArray[0]=_BusinessTripTarget;
		dfdArray[1]=_BusinessTripDays;
		dfdArray[2]=_BusinessTripSpent;		
		dfdArray[3]=_BankAccount;
				
		
		String[] exposedSteps=new String[]{"applyForReimbursement","approveReimbursementRequest","handleReimbursement"};
		
		BusinessActivityDefinition bsd=ActivityComponentFactory.createBusinessActivityDefinition("业务出差报销", "viewfunction_inc",exposedSteps);
		bsd.setActivityDataFields(dfdArray);		
		FileInputStream fileInputStream = new FileInputStream(new File("processDefine/业务出差报销.bpmn20.xml"));
		bsd.setDefinitionResource(fileInputStream);		
		
		DataFieldDefinition[] step1edfdArray=new DataFieldDefinition[4];
		step1edfdArray[0]=_BusinessTripTarget;
		step1edfdArray[1]=_BusinessTripDays;
		step1edfdArray[2]=_BusinessTripSpent;
		step1edfdArray[3]=_BankAccount;
		bsd.setActivityStepExposedDataFields("applyForReimbursement", step1edfdArray);
		bsd.setActivityStepRelatedRole("applyForReimbursement", "Sales Department");			
		
		DataFieldDefinition[] step2edfdArray=new DataFieldDefinition[3];
		step2edfdArray[0]=_BusinessTripTarget;
		step2edfdArray[1]=_BusinessTripDays;
		step2edfdArray[2]=_BusinessTripSpent;			
		bsd.setActivityStepExposedDataFields("approveReimbursementRequest", step2edfdArray);
		bsd.setActivityStepRelatedRole("approveReimbursementRequest", "Management Department");
		
		DataFieldDefinition[] step3edfdArray=new DataFieldDefinition[2];
		step3edfdArray[0]=_BusinessTripSpent;
		step3edfdArray[1]=_BankAccount;	
		bsd.setActivityStepExposedDataFields("handleReimbursement", step3edfdArray);
		bsd.setActivityStepRelatedRole("handleReimbursement", "Financial Department");		
		
		activitySpace.addBusinessActivityDefinition(bsd);		
	}
	
	public static void deployProcess_4() throws FileNotFoundException, ActivityEngineDataException, ActivityEngineProcessException, ActivityEngineRuntimeException, ActivityEngineActivityException{
		//产品出库
		ActivitySpace activitySpace=ActivityComponentFactory.getActivitySpace("viewfunction_inc");		
		
		DataFieldDefinition _ProductMode=ActivityComponentFactory.cteateDataFieldDefinition("ProductMode", PropertyType.STRING, false);
		_ProductMode.setDescription("ProductMode 产品型号");
		_ProductMode.setDisplayName("产品型号");
		_ProductMode.setMandatoryField(true);
		_ProductMode.setSystemField(false);
		
		DataFieldDefinition _ProductOrderNo=ActivityComponentFactory.cteateDataFieldDefinition("ProductOrderNo", PropertyType.STRING, false);
		_ProductOrderNo.setDescription("ProductOrderNo 产品订单号");
		_ProductOrderNo.setDisplayName("产品订单号");
		_ProductOrderNo.setMandatoryField(true);
		_ProductOrderNo.setSystemField(false);		
		
		DataFieldDefinition _ProductNumber=ActivityComponentFactory.cteateDataFieldDefinition("ProductNumber", PropertyType.DOUBLE, false);
		_ProductNumber.setDescription("ProductNumber 产品数量");
		_ProductNumber.setDisplayName("产品数量");
		_ProductNumber.setMandatoryField(true);
		_ProductNumber.setSystemField(false);
		
		DataFieldDefinition _ProductCheckStand=ActivityComponentFactory.cteateDataFieldDefinition("ProductCheckStand", PropertyType.STRING, false);
		_ProductCheckStand.setDescription("ProductCheckStand 产品质检标准");
		_ProductCheckStand.setDisplayName("产品质检标准");
		_ProductCheckStand.setMandatoryField(true);
		_ProductCheckStand.setSystemField(false);		
		
		DataFieldDefinition _ProductCheckNum=ActivityComponentFactory.cteateDataFieldDefinition("ProductCheckNum", PropertyType.DOUBLE, false);
		_ProductCheckNum.setDescription("ProductCheckNum 产品抽检数量");
		_ProductCheckNum.setDisplayName("产品抽检数量");
		_ProductCheckNum.setMandatoryField(true);
		_ProductCheckNum.setSystemField(false);		
		
		DataFieldDefinition _ProductShipAddress=ActivityComponentFactory.cteateDataFieldDefinition("ProductShipAddress", PropertyType.STRING, false);
		_ProductShipAddress.setDescription("ProductShipAddress 产品发货地址");
		_ProductShipAddress.setDisplayName("产品发货地址");
		_ProductShipAddress.setMandatoryField(true);
		_ProductShipAddress.setSystemField(false);		
		
		DataFieldDefinition[] dfdArray=new DataFieldDefinition[6];
		dfdArray[0]=_ProductMode;
		dfdArray[1]=_ProductOrderNo;
		dfdArray[2]=_ProductNumber;		
		dfdArray[3]=_ProductCheckStand;
		dfdArray[4]=_ProductCheckNum;
		dfdArray[5]=_ProductShipAddress;				
		
		String[] exposedSteps=new String[]{"finishProductCreation","productQA","productShip"};
		
		BusinessActivityDefinition bsd=ActivityComponentFactory.createBusinessActivityDefinition("产品出库", "viewfunction_inc",exposedSteps);
		bsd.setActivityDataFields(dfdArray);		
		FileInputStream fileInputStream = new FileInputStream(new File("processDefine/产品出库.bpmn20.xml"));
		bsd.setDefinitionResource(fileInputStream);		
		
		DataFieldDefinition[] step1edfdArray=new DataFieldDefinition[3];
		step1edfdArray[0]=_ProductMode;
		step1edfdArray[1]=_ProductOrderNo;
		step1edfdArray[2]=_ProductNumber;	
		bsd.setActivityStepExposedDataFields("finishProductCreation", step1edfdArray);
		bsd.setActivityStepRelatedRole("finishProductCreation", "Manufacturing Department");			
		
		DataFieldDefinition[] step2edfdArray=new DataFieldDefinition[4];
		step2edfdArray[0]=_ProductMode;
		step2edfdArray[1]=_ProductOrderNo;
		step2edfdArray[2]=_ProductCheckStand;
		step2edfdArray[3]=_ProductCheckNum;
		bsd.setActivityStepExposedDataFields("productQA", step2edfdArray);
		bsd.setActivityStepRelatedRole("productQA", "Quality Inspection Department");
		
		DataFieldDefinition[] step3edfdArray=new DataFieldDefinition[4];
		step3edfdArray[0]=_ProductMode;
		step3edfdArray[1]=_ProductOrderNo;	
		step3edfdArray[2]=_ProductNumber;	
		step3edfdArray[3]=_ProductShipAddress;			
		bsd.setActivityStepExposedDataFields("productShip", step3edfdArray);
		bsd.setActivityStepRelatedRole("productShip", "Distribution Department");		
		
		activitySpace.addBusinessActivityDefinition(bsd);		
	}
	
	public static void deployProcess_5() throws FileNotFoundException, ActivityEngineDataException, ActivityEngineProcessException, ActivityEngineRuntimeException, ActivityEngineActivityException{
		//产品出库
		ActivitySpace activitySpace=ActivityComponentFactory.getActivitySpace("viewfunction_inc");				
		
		String[] exposedSteps=new String[]{"submitVacationRequest","approveVocationRequest","confirmVacationRequest"};
		
		BusinessActivityDefinition bsd=ActivityComponentFactory.createBusinessActivityDefinition("员工休假申请", "viewfunction_inc",exposedSteps);			
		FileInputStream fileInputStream = new FileInputStream(new File("processDefine/员工休假申请.bpmn20.xml"));
		bsd.setDefinitionResource(fileInputStream);			
		bsd.setActivityStepRelatedRole("approveVocationRequest", "Management Department");		
		bsd.setLaunchUserIdentityAttributeName("vacationRequestor");
		activitySpace.addBusinessActivityDefinition(bsd);		
	}
	
	public static void createRoster(){
		ActivitySpace activitySpace=ActivityComponentFactory.getActivitySpace("viewfunction_inc");	
		Roster roster1=ActivityComponentFactory.createRoster("viewfunction_inc", "AdminRoster_1");
		roster1.setDescription("Roster_1Description");
		roster1.setDisplayName("Roster_1Dispalyname");		
		Roster roster2=ActivityComponentFactory.createRoster("viewfunction_inc", "AdminRoster_2");
		roster2.setDescription("Roster_2Description");
		roster2.setDisplayName("Roster_2Dispalyname");		
		try {
			activitySpace.addRoster(roster1);
			activitySpace.addRoster(roster2);			
		} catch (ActivityEngineRuntimeException e) {			
			e.printStackTrace();
		}		
	}
	
	public static void main(String[] args) throws Exception{		
		tearDown();	
		
		System.out.println("1");
		loadStructure();
		System.out.println("2");
		deployProcess_1();
		System.out.println("3");
		deployProcess_2();
		System.out.println("4");
		deployProcess_3();
		System.out.println("5");
		deployProcess_4();
		System.out.println("6");	
		deployProcess_5();		
		
		createRoster();
		System.out.println("Finish");
	}
	
	
	public static void tearDown() throws Exception {		
		System.out.println("remove Used ContentSpace");				
		boolean delResult0=false;
		File workspacesDir=new File("contentRepository/workspaces");
		delResult0=deleteDirectory(workspacesDir);
		if(delResult0){
			System.out.println("workspaces removed!!");
		}
		boolean delResult=false;
		File versionDir=new File("contentRepository/version");
		delResult=deleteDirectory(versionDir);
		if(delResult){
			System.out.println("version removed!!");
		}			
		boolean delResult2=false;
		File repositoryDir=new File("contentRepository/repository");
		delResult2=deleteDirectory(repositoryDir);
		if(delResult2){
			System.out.println("repository removed!!");
		}			
	}	
	private static boolean deleteDirectory(File path) {
		    if( path.exists() ) {
		      File[] files = path.listFiles();
		      for(int i=0; i<files.length; i++) {
		         if(files[i].isDirectory()) {
		           deleteDirectory(files[i]);
		         }
		         else {
		           files[i].delete();
		         }
		      }
		    }
		    return( path.delete() );
	}		
	

}

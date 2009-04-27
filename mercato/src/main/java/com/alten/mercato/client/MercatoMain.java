/**
 * 
 */
package com.alten.mercato.client;

import java.util.List;

import com.alten.mercato.client.service.PersonService;
import com.alten.mercato.client.service.TransferService;
import com.alten.mercato.client.service.UserService;
import com.alten.mercato.client.ui.framework.widget.CustomWaitDialog;
import com.alten.mercato.client.ui.util.ConstantsMercato;
import com.alten.mercato.server.controller.dto.InfoTransfer;
import com.alten.mercato.server.model.Departement;
import com.alten.mercato.server.model.Personne;
import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.RootPanel;
import com.smartgwt.client.data.DSCallback;
import com.smartgwt.client.data.DSRequest;
import com.smartgwt.client.data.DSResponse;
import com.smartgwt.client.types.Alignment;
import com.smartgwt.client.types.DragDataAction;
import com.smartgwt.client.types.TreeModelType;
import com.smartgwt.client.util.SC;
import com.smartgwt.client.widgets.Canvas;
import com.smartgwt.client.widgets.HTMLPane;
import com.smartgwt.client.widgets.events.DragStartEvent;
import com.smartgwt.client.widgets.events.DragStartHandler;
import com.smartgwt.client.widgets.events.DragStopEvent;
import com.smartgwt.client.widgets.events.DragStopHandler;
import com.smartgwt.client.widgets.grid.ListGridRecord;
import com.smartgwt.client.widgets.grid.events.CellDoubleClickEvent;
import com.smartgwt.client.widgets.grid.events.CellDoubleClickHandler;
import com.smartgwt.client.widgets.grid.events.CellSavedEvent;
import com.smartgwt.client.widgets.grid.events.CellSavedHandler;
import com.smartgwt.client.widgets.layout.HLayout;
import com.smartgwt.client.widgets.layout.VLayout;
import com.smartgwt.client.widgets.toolbar.ToolStrip;
import com.smartgwt.client.widgets.tree.Tree;
import com.smartgwt.client.widgets.tree.TreeGrid;
import com.smartgwt.client.widgets.tree.TreeGridField;
import com.smartgwt.client.widgets.tree.TreeNode;


/**
 * @author Huage Chen
 *
 */
public class MercatoMain implements EntryPoint {
	private ConsultantTreeGrid tgMyDepartmentConsultants = new ConsultantTreeGrid();
	private ConsultantTreeGrid tgOtherDepartmentConsultants = new ConsultantTreeGrid();
	private TreeNode rootNodeMyDepartmentConsultants = null;
	private TreeNode rootNodeOtherDepartmentConsultants = null;
	private ListGridRecord myDepartmentDraggedRecord = null;
	private ListGridRecord otherDepartmentDraggedRecord = null;
	private String myValidatingNodeId = "";
	private String otherValidatingNodeId = "";

	

	/* (non-Javadoc)
	 * @see com.google.gwt.core.client.EntryPoint#onModuleLoad()
	 */
	public void onModuleLoad() {
		getCurrentUserByRPC();	
	}
	
	private void getCurrentUserByRPC() {
		AsyncCallback<Personne> callback = new AsyncCallback<Personne>() {

			public void onFailure(Throwable t) {
				SC.say("fail to load");
			}

			public void onSuccess(Personne result) {
				ConstantsMercato.initCurrentUser(result);
				if (result != null) {
					System.out.println(result.getPerNom());
				}
				initLayout();
			}
		};
		UserService.Util.getInstance().getCurrentUser(callback);
	}

	private void initLayout() {
		VLayout main = new VLayout() ;
		main.setMembersMargin(5);  
		main.setPadding(5);
		main.setAlign(Alignment.LEFT);
		
		final ToolStrip toolbar = new ToolStrip();		
		toolbar.setHeight(40);
		toolbar.setWidth(1200);
		toolbar.setPadding(5);
		toolbar.setMargin(5);
	

		HLayout hlConsultants = new HLayout();  
		hlConsultants.setWidth(1200);  
		hlConsultants.setHeight(800);  
		hlConsultants.setMembersMargin(5);  
		hlConsultants.setPadding(5);

		initMyDepartmentTreeGrid();
		//loadListConsultantsByRPC();

		initOtherDepartmentTreeGrid();
		loadOtherDepartmentsByRPC();
		//loadOtherDepartmentListConsultantsByRPC();
		
		loadConsultantTransferInfoByRPC();

		hlConsultants.addMember(tgMyDepartmentConsultants);
		hlConsultants.addMember(tgOtherDepartmentConsultants);

		HTMLPane paneLinkLogout = new HTMLPane();
		paneLinkLogout.setContents("<a href=\"/mercato/destroySession\">Logout</a>");
		paneLinkLogout.setHeight(20);

		Canvas canvasLogout = new Canvas();
		canvasLogout.addChild(paneLinkLogout);
		
		Personne user = ConstantsMercato.getCurrentUser();
		
		if (user != null ) {
			System.out.println(user.getPerNom());
		}
		
		toolbar.setContents("Welcome " + user.getPerPrenom() + " " + user.getPerNom() + " This sample application let you transfer consultants between your department and other department under the control of the predifined workflows");

		main.addMember(toolbar);
		main.addMember(hlConsultants);
		Canvas canvas = new Canvas();
		canvas.addChild(main);
		
		RootPanel.get().add(canvas); 
		RootPanel.get().add(canvasLogout);

		// clean the load message
		RootPanel.get("loadingWrapper").getElement().setInnerHTML("");
	}


	
	/**
	 * 
	 */
	private void initMyDepartmentTreeGrid() {
		// define the tree fields
		TreeGridField name = new TreeGridField(ConstantsMercato.KEY_LABEL,"Consultant");
		name.setCanEdit(false);
		TreeGridField originalDpmt = new TreeGridField(ConstantsMercato.KEY_ORIGINAL_DEPARTMENT,"Current dpmt",80);
		name.setCanEdit(false);
		TreeGridField matricule = new TreeGridField(ConstantsMercato.KEY_MATRICULE, "Reference Num",100);
		matricule.setCanEdit(false);
		TreeGridField validation = new TreeGridField(ConstantsMercato.KEY_VALIDATION,"Validation", 150);
		validation.setValueMap("Validate", "Cancel");
		validation.setCanEdit(true);
		tgMyDepartmentConsultants.setFields(name,originalDpmt,matricule, validation);
		
		
		//define the root nodes
		Tree tree = new Tree();
		tree.setModelType(TreeModelType.PARENT);
		TreeNode rootNode = createRootNode("root", "My department", ConstantsMercato.ICON_DPMT, null);
		rootNode.setCanAcceptDrop(false);
		tree.setRoot(rootNode);
		rootNodeMyDepartmentConsultants = createRootNode("rootMyDpmt", "My department", ConstantsMercato.ICON_DPMT, null);
		tree.add(rootNodeMyDepartmentConsultants, rootNode);
		tgMyDepartmentConsultants.setData(tree);
		tree.setNameProperty(ConstantsMercato.KEY_LABEL);
		tgMyDepartmentConsultants.getData().openAll();
		
		tgMyDepartmentConsultants.addDragStartHandler(new DragStartHandler() {

			public void onDragStart(DragStartEvent event) {
				ListGridRecord record = tgMyDepartmentConsultants.getSelectedRecord();
				
				if (record.getAttribute(ConstantsMercato.KEY_TYPE).equals("folder")) {
					event.cancel();
				}
				setMyDepartmentDraggedRecord(tgMyDepartmentConsultants.getSelectedRecord());
			}
			
		});
		
		tgMyDepartmentConsultants.addDragStopHandler(new DragStopHandler() {

			public void onDragStop(DragStopEvent event) {
				System.out.println("drag stopped..");
				ListGridRecord record= getMyDepartmentDraggedRecord();
				if (record != null) {
					System.out.println("Record dragged " + record.getAttribute(ConstantsMercato.KEY_LABEL));
					
					TreeNode treeNode = tgOtherDepartmentConsultants.getTree().findById(record.getAttribute(ConstantsMercato.KEY_ID));
					if (treeNode!=null) {
						TreeNode parent = tgOtherDepartmentConsultants.getTree().getParent(treeNode);
						System.out.println("New parent " + parent.getAttribute(ConstantsMercato.KEY_LABEL));
					}
				}
				setMyDepartmentDraggedRecord(null);
			}
			
		});
		
		tgMyDepartmentConsultants.addCellDoubleClickHandler(new CellDoubleClickHandler() {

			public void onCellDoubleClick(CellDoubleClickEvent event) {
				ListGridRecord record = tgMyDepartmentConsultants.getSelectedRecord();
				if (!record.getAttribute(ConstantsMercato.KEY_VALIDATION).equals("Please validate")) {
					event.cancel();
					return;
				}
				setMyValidatingNodeId(record.getAttribute(ConstantsMercato.KEY_ID));
				System.out.println("Trying to change " + record.getAttribute(ConstantsMercato.KEY_LABEL));
			}
			
		});
		
		tgMyDepartmentConsultants.addCellSavedHandler(new CellSavedHandler() {

			public void onCellSaved(CellSavedEvent event) {
				TreeNode treeNode = tgMyDepartmentConsultants.getTree().findById(getMyValidatingNodeId());
				System.out.println("validation changed!");
				if (treeNode != null) {
					System.out.println("To " + treeNode.getAttribute("validation") + " status.");
				}
			}
			
		});

	}

	
	/**
	 * 
	 */
	private void initOtherDepartmentTreeGrid() {
		
		// define the tree fields
		TreeGridField name = new TreeGridField(ConstantsMercato.KEY_LABEL,"Consultant");
		name.setCanEdit(false);
		TreeGridField matricule = new TreeGridField(ConstantsMercato.KEY_MATRICULE, "Reference Num",100);
		matricule.setCanEdit(false);
		TreeGridField validation = new TreeGridField(ConstantsMercato.KEY_VALIDATION,"Validation", 150);
		validation.setValueMap("Validate", "Cancel");
		validation.setCanEdit(true);
		tgOtherDepartmentConsultants.setFields(name,matricule, validation);

		Tree tree = new Tree();
		tree.setModelType(TreeModelType.PARENT);
		TreeNode rootNode = createRootNode("root", "Other departments", ConstantsMercato.ICON_DPMT, null);
		rootNode.setCanAcceptDrop(false);
		tree.setRoot(rootNode);
		rootNodeOtherDepartmentConsultants = createRootNode("rootOtherDpmt", "Other departments", ConstantsMercato.ICON_DPMT, null);
		rootNodeOtherDepartmentConsultants.setCanAcceptDrop(false);
		tree.add(rootNodeOtherDepartmentConsultants, rootNode);
		tree.setNameProperty(ConstantsMercato.KEY_LABEL);
		tgOtherDepartmentConsultants.setData(tree);
		tgOtherDepartmentConsultants.getData().openAll();
		
		tgOtherDepartmentConsultants.addDragStartHandler(new DragStartHandler() {

			public void onDragStart(DragStartEvent event) {
				ListGridRecord record = tgOtherDepartmentConsultants.getSelectedRecord();
				
				if (record.getAttribute(ConstantsMercato.KEY_TYPE).equals("folder")) {
					event.cancel();
				}
				
				setOtherDepartmentDraggedRecord(tgOtherDepartmentConsultants.getSelectedRecord());
			}
			
		});
		
		tgOtherDepartmentConsultants.addDragStopHandler(new DragStopHandler() {

			public void onDragStop(DragStopEvent event) {
				ListGridRecord record= getOtherDepartmentDraggedRecord();
				if (record != null) {
					System.out.println("Record dragged " + record.getAttribute(ConstantsMercato.KEY_LABEL));
					
					TreeNode treeNode = tgMyDepartmentConsultants.getTree().findById(record.getAttribute(ConstantsMercato.KEY_ID));
					if (treeNode!=null) {
						TreeNode parent = tgMyDepartmentConsultants.getTree().getParent(treeNode);
						System.out.println("New parent " + parent.getAttribute(ConstantsMercato.KEY_LABEL));
						//start the workflow process
						System.out.println("starting transfer request for consultant " + treeNode.getAttribute(ConstantsMercato.KEY_LABEL) + " from " + treeNode.getAttribute(ConstantsMercato.KEY_DEPARTMENT) + " to " + ConstantsMercato.getCurrentUser().getDepartement().getDepLib() );
						startTransferRequestByRPC(ConstantsMercato.getCurrentUser().getDepartement().getDepId(), new Long(treeNode.getAttribute(ConstantsMercato.KEY_ID)).longValue());
					}
				}
				setOtherDepartmentDraggedRecord(null);
				
			}
			
		});
		
		tgOtherDepartmentConsultants.addCellDoubleClickHandler(new CellDoubleClickHandler() {

			public void onCellDoubleClick(CellDoubleClickEvent event) {
				ListGridRecord record = tgOtherDepartmentConsultants.getSelectedRecord();
				if (!record.getAttribute(ConstantsMercato.KEY_VALIDATION).equals("Please validate")) {
					event.cancel();
					return;
				}
				setOtherValidatingNodeId(record.getAttribute(ConstantsMercato.KEY_ID));
				System.out.println("Trying to change " + record.getAttribute(ConstantsMercato.KEY_LABEL));
				
				
			}
			
		});
		
		tgOtherDepartmentConsultants.addCellSavedHandler(new CellSavedHandler() {

			public void onCellSaved(CellSavedEvent event) {
				TreeNode treeNode = tgOtherDepartmentConsultants.getTree().findById(getOtherValidatingNodeId());
				System.out.println("validation changed!");
				if (treeNode != null) {
					System.out.println("To " + treeNode.getAttribute("validation") + " status.");
				}
			}
			
		});

	}
	
	

	private void loadConsultantTransferInfoByRPC() {
		AsyncCallback<List<InfoTransfer>> callback = new AsyncCallback<List<InfoTransfer>>() {

			public void onFailure(Throwable arg0) {
				// TODO Auto-generated method stub
				
			}

			public void onSuccess(List<InfoTransfer> result) {
				if ((result!=null) && (result.size()>0)) {
					createConsultantTreeNodes(result);
				}
				
			}
			
		};
		TransferService.Util.getInstance().getConsultantWithTransferInfo(callback);
	}

	/**
	 * 
	 */
	private void loadOtherDepartmentsByRPC() {


		AsyncCallback<List<Departement>> callback = new AsyncCallback<List<Departement>>() {

			public void onSuccess(List<Departement> result) {
				if (result==null||result.size()==0) {
					return;
				}
				System.out.println(result.size() + " department for this dd found");

				createOtherDepartmentTreeNodes(result);
			}

			public void onFailure(Throwable ex) {
			}
		};

		PersonService.Util.getInstance().getOtherDepartements(ConstantsMercato.getCurrentUser().getDepartement().getDepId(),callback);
	}
	
	private void startTransferRequestByRPC(long transDepEntrId, long transDepConsulId) {
		final CustomWaitDialog dlg = CustomWaitDialog.getInstance();
		
		AsyncCallback<Personne> callback = new AsyncCallback<Personne>() {

			public void onSuccess(Personne result) {
				if (result!=null) {
					dlg.hide();
					updateRequestedTreeNode(result);
					return;
				}

			}

			public void onFailure(Throwable ex) {
				dlg.hide();
				SC.say(ex.getMessage());
			}
		};

		TransferService.Util.getInstance().startAndAskTransferProcess(transDepEntrId, transDepConsulId, callback);
		// give user a wait message while retrieving datas
		dlg.show();
	}

	private void updateRequestedTreeNode(Personne pers) {
		Tree treeMyDepartment = tgMyDepartmentConsultants.getTree();
		
		TreeNode oldTreeNode = treeMyDepartment.findById(String.valueOf(pers.getPerId()));
		treeMyDepartment.remove(oldTreeNode);
		
		TreeNode parentNode = treeMyDepartment.findById("rootMyDpmt");
		TreeNode node = new TreeNode();
		node.setAttribute(ConstantsMercato.KEY_ID, String.valueOf(pers.getPerId()));
		node.setAttribute(ConstantsMercato.KEY_LABEL, pers.getPerPrenom() + " "+ pers.getPerNom());
		node.setAttribute(ConstantsMercato.KEY_TYPE, "node");
		node.setAttribute(ConstantsMercato.KEY_MATRICULE, pers.getPerMatricule());
		node.setAttribute(ConstantsMercato.KEY_ICON, ConstantsMercato.ICON_USER_BLUE);
		node.setAttribute(ConstantsMercato.KEY_DEPARTMENT, pers.getDepartement().getDepLib());
		node.setAttribute(ConstantsMercato.KEY_ORIGINAL_DEPARTMENT, pers.getDepartement().getDepLib());
		node.setAttribute(ConstantsMercato.KEY_VALIDATION, "Waiting for confirmation");
		node.setAttribute(ConstantsMercato.KEY_OBJECT, pers);
		node.setCanAcceptDrop(false);
		node.setIsFolder(false);
		node.setEnabled(false);
		
		treeMyDepartment.add(node, parentNode);
		tgMyDepartmentConsultants.setData(treeMyDepartment);
		tgMyDepartmentConsultants.getData().openAll();
		
	}
	
	private void setMyDepartmentDraggedRecord(ListGridRecord record) {
		myDepartmentDraggedRecord = record;
	}
	
	private ListGridRecord getMyDepartmentDraggedRecord() {
		return this.myDepartmentDraggedRecord;
	}
	
	
	private void setOtherDepartmentDraggedRecord(ListGridRecord record) {
		otherDepartmentDraggedRecord = record;
	}
	
	private ListGridRecord getOtherDepartmentDraggedRecord() {
		return this.otherDepartmentDraggedRecord;
	}
	
	/**
	 * @return the validatingNodeId
	 */
	private String getMyValidatingNodeId() {
		return myValidatingNodeId;
	}

	/**
	 * @param validatingNodeId the validatingNodeId to set
	 */
	private void setMyValidatingNodeId(String validatingNodeId) {
		this.myValidatingNodeId = validatingNodeId;
	}
	
	/**
	 * @return the otherValidatingNodeId
	 */
	private String getOtherValidatingNodeId() {
		return otherValidatingNodeId;
	}

	/**
	 * @param otherValidatingNodeId the otherValidatingNodeId to set
	 */
	private void setOtherValidatingNodeId(String otherValidatingNodeId) {
		this.otherValidatingNodeId = otherValidatingNodeId;
	}

	private TreeNode createRootNode(String id, String label, String icon, Object obj) {
		TreeNode node = new TreeNode();
		node.setAttribute(ConstantsMercato.KEY_ID, id);
		node.setAttribute(ConstantsMercato.KEY_LABEL, label);
		node.setAttribute(ConstantsMercato.KEY_ICON, icon);
		node.setAttribute(ConstantsMercato.KEY_MATRICULE, "");
		node.setAttribute(ConstantsMercato.KEY_DEPARTMENT, "");
		node.setAttribute(ConstantsMercato.KEY_OBJECT, obj);
		node.setAttribute(ConstantsMercato.KEY_TYPE, "folder");
		node.setCanDrag(false);
		node.setIsFolder(true);
		return node;
	}


	private void createConsultantTreeNodes(List<InfoTransfer> infoTransfers) {
		Tree treeMyDepartment = tgMyDepartmentConsultants.getTree();
		Tree treeOtherDepartment = tgOtherDepartmentConsultants.getTree();
		for (InfoTransfer infoTransfer: infoTransfers) {
			
			
			TreeNode node = new TreeNode();
			node.setAttribute(ConstantsMercato.KEY_ID, String.valueOf(infoTransfer.getPersonne().getPerId()));
			node.setAttribute(ConstantsMercato.KEY_LABEL, infoTransfer.getPersonne().getPerPrenom() + " "+ infoTransfer.getPersonne().getPerNom());
			node.setAttribute(ConstantsMercato.KEY_TYPE, "node");
			node.setAttribute(ConstantsMercato.KEY_MATRICULE, infoTransfer.getPersonne().getPerMatricule());
			node.setAttribute(ConstantsMercato.KEY_DEPARTMENT, infoTransfer.getPersonne().getDepartement().getDepLib());
			node.setAttribute(ConstantsMercato.KEY_OBJECT, infoTransfer.getPersonne());
			node.setCanAcceptDrop(false);
			node.setIsFolder(false);
			
			// Check if it is my departement
			if (infoTransfer.getPersonne().getDepartement().getDepId() == ConstantsMercato.getCurrentUser().getDepartement().getDepId()) {
				node.setAttribute(ConstantsMercato.KEY_ICON, ConstantsMercato.ICON_USER);
				
				// if the personne has no transfer
				if (infoTransfer.getPersonne().getTransferCourant() == null) {
					TreeNode parentNode = treeMyDepartment.findById("rootMyDpmt");
					node.setCanDrag(true);
					treeMyDepartment.add(node, parentNode);
				} else {
					//TODO modify after the introduction of transfer proposal
					TreeNode parentNode = treeOtherDepartment.findById(String.valueOf(infoTransfer.getPersonne().getTransferCourant().getDepEntr().getDepId()));
					node.setCanDrag(false);
					node.setAttribute(ConstantsMercato.KEY_VALIDATION, "Please validate");
					treeOtherDepartment.add(node, parentNode);
				}
			} else {
				node.setAttribute(ConstantsMercato.KEY_ICON, ConstantsMercato.ICON_USER_BLUE);
				if (infoTransfer.getPersonne().getTransferCourant() == null) {
					TreeNode parentNode = treeOtherDepartment.findById(String.valueOf(infoTransfer.getPersonne().getDepartement().getDepId()));
					node.setCanDrag(true);
					treeOtherDepartment.add(node, parentNode);
				} else {
					if (infoTransfer.getPersonne().getTransferCourant().getDepEntr().getDepId() == ConstantsMercato.getCurrentUser().getDepartement().getDepId()) {
						//TODO modify after the introduction of transfer proposal
						TreeNode parentNode = treeMyDepartment.findById("rootMyDpmt");
						node.setAttribute(ConstantsMercato.KEY_ORIGINAL_DEPARTMENT, infoTransfer.getPersonne().getDepartement().getDepLib());
						node.setAttribute(ConstantsMercato.KEY_VALIDATION, "Waiting for confirmation");
						node.setCanDrag(false);
						node.setEnabled(false);
						treeMyDepartment.add(node, parentNode);
					}
				}
			}
		}

		tgMyDepartmentConsultants.setData(treeMyDepartment);
		tgMyDepartmentConsultants.getData().openAll();
		tgOtherDepartmentConsultants.setData(treeOtherDepartment);
		tgOtherDepartmentConsultants.getData().openAll();
	}
	/**
	 * Create the department list tree node without the own department
	 * @param departements
	 */
	private void createOtherDepartmentTreeNodes(List<Departement> departements) {


		Tree tree = tgOtherDepartmentConsultants.getTree();
		Object obj = null;
		for (Departement departement: departements) {
			TreeNode node = new TreeNode();
			node.setAttribute(ConstantsMercato.KEY_ID, String.valueOf(departement.getDepId()));
			node.setAttribute(ConstantsMercato.KEY_LABEL, departement.getDepLib());
			node.setAttribute(ConstantsMercato.KEY_ICON, ConstantsMercato.ICON_DPMT);
			node.setAttribute(ConstantsMercato.KEY_MATRICULE, "");
			node.setAttribute(ConstantsMercato.KEY_OBJECT, obj);
			node.setAttribute(ConstantsMercato.KEY_TYPE, "folder");
			node.setIsFolder(true);
			node.setCanDrag(false);
			TreeNode parentNode = tree.findById("rootOtherDpmt");
			tree.add(node, parentNode);
		}

		tgOtherDepartmentConsultants.setData(tree);
		tgOtherDepartmentConsultants.getData().openAll();
	}



	/**
	 * @param treegrid
	 */
	public void openAllNodesOfListGrid(final TreeGrid treegrid) {
		Timer timer = new Timer() {

			@Override
			public void run() {
				if (!treegrid.isDrawn()) {
					return;
				}
				treegrid.selectAllRecords();
				// fetch data to copy all element from datasource to inner tree of listGrid
				treegrid.fetchData(null, new DSCallback() {

					public void execute(DSResponse dsresponse, Object obj, DSRequest dsrequest) {
						if (treegrid.getTree()!=null) {
							treegrid.getTree().openAll();
							treegrid.selectAllRecords();
							endTimerAndPostProcess();
						}
					}
				});
				return;
			}

			/**
			 * cancel the timer and update a virtual field of the datasource (workaround to allow inline edit)
			 */
			private void endTimerAndPostProcess() {
				cancel();
				treegrid.deselectAllRecords();
			}
		};
		timer.scheduleRepeating(1000);
	}

	/**
	 * custom tree grid for both my department consultant treegrid and other department consultant treegrd
	 * @author Huage Chen
	 *
	 */
	public static class ConsultantTreeGrid extends TreeGrid {  
		public ConsultantTreeGrid() {  
			super();
			setHeight100();
			setWidth("50%");
			setCanDragRecordsOut(true);
			setDragDataAction(DragDataAction.MOVE);
			setCanAcceptDroppedRecords(true);
			setAlternateRecordStyles(true);
			setShowHeader(true);
			setLeaveScrollbarGap(false);
			setEditByCell(true);
			setCanReorderRecords(false);
			setCanReparentNodes(false);
			
		}  
		
		@Override
		public Boolean willAcceptDrop() {
			return true;
		}

	}  
}

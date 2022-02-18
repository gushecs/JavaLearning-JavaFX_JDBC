package gui;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Set;

import db.DBException;
import gui.listeners.DataChangeListener;
import gui.util.Alerts;
import gui.util.Constraints;
import gui.util.Utils;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import model.entities.Seller;
import model.exceptions.ValidationException;
import model.services.SellerService;

public class SellerFormController implements Initializable {

	@FXML
	private Button btSave;
	@FXML
	private Button btCancel;
	@FXML
	private TextField txtId;
	@FXML
	private TextField txtName;
	@FXML
	private Label labelErrorId;
	@FXML
	private Label labelErrorName;
	private Seller entity;
	private SellerService service;
	private List<DataChangeListener> dataChangeListeners = new ArrayList<>();

	public void setSeller(Seller entity) {
		this.entity = entity;
	}

	public void subscribeDataChangeListener(DataChangeListener listener) {
		dataChangeListeners.add(listener);
	}

	public void setSellerService(SellerService service) {
		this.service = service;
	}

	@FXML
	public void onBtSaveAction(ActionEvent event) {
		if (entity == null) {
			throw new IllegalStateException("Entity is null");
		}
		if (service == null) {
			throw new IllegalStateException("Service is null");
		}
		try {
			entity = getFormData();
			service.saveOrUpdate(entity);
			notifyDataChangeListeners();
			Utils.currentStage(event).close();
		} catch (DBException e) {
			Alerts.showAlert("Error saving object", null, e.getMessage(), AlertType.ERROR);
		} catch (ValidationException e) {
			setErrorMessages(e.getErrors());
		}
	}

	private void notifyDataChangeListeners() {
		
		dataChangeListeners.forEach(x -> x.onDataChanged());
		
	}

	private Seller getFormData() {
		ValidationException exception = new ValidationException("Validation error");
		Seller obj = new Seller();
		obj.setId(Utils.tryParseToInt(txtId.getText()));
		if (txtName.getText()==null || txtName.getText().trim().equals("")) {
			exception.addError("name", "Field can't be empty.");
		}
		obj.setName(txtName.getText());
		if (exception.getErrors().size()>0) {
			throw exception;
		}
		return obj;
	}

	@FXML
	public void onBtCancelAction(ActionEvent event) {
		Utils.currentStage(event).close();
	}

	@Override
	public void initialize(URL url, ResourceBundle rb) {

		Constraints.setTextFieldInteger(txtId);
		Constraints.setTextFieldMaxLength(txtName, 30);

	}

	public void updateFormData() {
		if (entity == null) {
			throw new IllegalStateException("Entity was null");
		}
		txtId.setText(String.valueOf(entity.getId()));
		txtName.setText(entity.getName());
	}
	
	private void setErrorMessages(Map<String,String> errors) {
		Set<String> fields = errors.keySet();
		if (fields.contains("name")) {
			labelErrorName.setText(errors.get("name"));
		}
	}

}

package gui;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Set;

import db.DbException;
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
import model.entities.Department;
import model.exceptions.ValidationException;
import model.services.DepartmentService;

public class DepartmentFormController implements Initializable {

	@FXML
	private TextField txtId;

	@FXML
	private TextField txtName;

	@FXML
	private Label labelErrorName;

	@FXML
	private Button btSave;

	@FXML
	private Button btCancel;

	private Department entity;

	private DepartmentService service;

	private List<DataChangeListener> dataChangeListeners = new ArrayList<>();

	public void setDepartment(Department entity) {
		this.entity = entity;
	}

	public void setDepartmentService(DepartmentService service) {
		this.service = service;
	}

	public void subscribeDataChangeListener(DataChangeListener listener) {
		dataChangeListeners.add(listener);
	}

	@FXML
	public void onBtSaveAction(ActionEvent event) {
		if (entity == null) {
			throw new IllegalStateException("Entidade está nula.");
		}
		if (service == null) {
			throw new IllegalStateException("Serviço está nula.");
		}
		try {
			entity = getFormData();
			service.saveOrUpdate(entity);
			notifyDataChangeListener();
			Utils.currentStage(event).close();
		} catch (ValidationException e) {
			setErrorsMessages(e.getErrors());
		} catch (DbException e) {
			Alerts.showAlert("Erro ao salvar objeto", null, e.getMessage(), AlertType.ERROR);
		}
	}

	private void notifyDataChangeListener() {
		for (DataChangeListener listener : dataChangeListeners) {
			listener.onDataChanged();
		}
	}

	private Department getFormData() {

		ValidationException exception = new ValidationException("Validation error");
		Department obj = new Department();

		obj.setId(Utils.tryParseToInt(txtId.getText()));

		if (txtName.getText() == null || txtName.getText().trim().equals("")) {
			exception.addErrors("name", "O campo não pode ser vazio. ");
		}
		obj.setName(txtName.getText());

		if (exception.getErrors().size() > 0) {
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

	}

	private void initializeNodes() {
		Constraints.setTextFieldInteger(txtId);
		Constraints.setTextFieldMaxLength(txtName, 30);
	}

	public void updateFormDate() {
		if (entity == null) {
			throw new IllegalStateException("Entidade é nula. ");
		}
		txtId.setText(String.valueOf(entity.getId()));
		txtName.setText(entity.getName());
	}

	private void setErrorsMessages(Map<String, String> errors) {
		Set<String> fields = errors.keySet();

		if (fields.contains("name")) {
			labelErrorName.setText(errors.get("name"));
		}

	}
}

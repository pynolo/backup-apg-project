package it.giunti.apg.client.services;

import java.util.List;

import com.google.gwt.user.client.rpc.AsyncCallback;

import it.giunti.apg.shared.model.FileUploads;
import it.giunti.apg.shared.model.RinnoviMassivi;

public interface UtilServiceAsync {
	//Install info
	void getApgTitle(AsyncCallback<String> callback);
	void getApgStatus(AsyncCallback<String> callback);
	void getApgMenuImage(AsyncCallback<String> callback);
	void getApgLoginImage(AsyncCallback<String> callback);
	void getApgVersion(AsyncCallback<String> callback);
	void getApguiInstallInfo(String appBaseUrl, AsyncCallback<String> callback);
	void getApgwsInstallInfo(String appBaseUrl, AsyncCallback<String> callback);
	void getApgautomationInstallInfo(String appBaseUrl, AsyncCallback<String> callback);

	//Rinnovi massivi
	void findRinnoviMassivi(Integer idPeriodico, AsyncCallback<List<RinnoviMassivi>> callback);
	void saveOrUpdateRinnoviMassiviList(List<RinnoviMassivi> rinnoviMassiviList,
			AsyncCallback<Boolean> callback);
	void deleteRinnovoMassivo(Integer idRinnovoMassivo, AsyncCallback<Boolean> callback);
	
	//File upload
	void findFileUploadsStripped(AsyncCallback<List<FileUploads>> callback);
	void deleteFileUpload(Integer idFileUpload, AsyncCallback<Boolean> callback);

}

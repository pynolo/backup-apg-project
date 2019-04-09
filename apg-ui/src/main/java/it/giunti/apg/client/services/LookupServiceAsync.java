package it.giunti.apg.client.services;

import java.util.Date;
import java.util.List;

import com.google.gwt.user.client.rpc.AsyncCallback;

import it.giunti.apg.shared.model.Adesioni;
import it.giunti.apg.shared.model.AliquoteIva;
import it.giunti.apg.shared.model.FileResources;
import it.giunti.apg.shared.model.FileUploads;
import it.giunti.apg.shared.model.Macroaree;
import it.giunti.apg.shared.model.Nazioni;
import it.giunti.apg.shared.model.Periodici;
import it.giunti.apg.shared.model.Professioni;
import it.giunti.apg.shared.model.Province;
import it.giunti.apg.shared.model.RinnoviMassivi;
import it.giunti.apg.shared.model.Societa;
import it.giunti.apg.shared.model.TipiDisdetta;
import it.giunti.apg.shared.model.TitoliStudio;

public interface LookupServiceAsync {
	void getApgTitle(AsyncCallback<String> callback);
	void getApgStatus(AsyncCallback<String> callback);
	void getApgMenuImage(AsyncCallback<String> callback);
	void getApgLoginImage(AsyncCallback<String> callback);
	void getApgVersion(AsyncCallback<String> callback);
	void findPeriodici(AsyncCallback<List<Periodici>> callback);
	void findPeriodici(Date extractionDt, AsyncCallback<List<Periodici>> callback);
	void findPeriodici(Integer selectedId, Date extractionDt, AsyncCallback<List<Periodici>> callback);
	void findSocietaById(String idSocieta, AsyncCallback<Societa> callback);
	void findProvince(AsyncCallback<List<Province>> callback);
	void findNazioni(AsyncCallback<List<Nazioni>> callback);
	void findProfessioni(AsyncCallback<List<Professioni>> callback);
	void findTitoliStudio(AsyncCallback<List<TitoliStudio>> callback);
	void findMacroaree(AsyncCallback<List<Macroaree>> callback);
	void findTipiDisdetta(AsyncCallback<List<TipiDisdetta>> callback);
	void findAliquoteIva(Date selectionDate, AsyncCallback<List<AliquoteIva>> callback);
	void findFileResources(String fileType, AsyncCallback<List<FileResources>> callback);
	void findFileUploadsStripped(AsyncCallback<List<FileUploads>> callback);
	void deleteFileUpload(Integer idFileUpload, AsyncCallback<Boolean> callback);
	
	void findAdesioni(String filterPrefix, int offset, int pageSize,
			AsyncCallback<List<Adesioni>> callback);
	void saveOrUpdateAdesione(Adesioni adesioni, AsyncCallback<Integer> callback);
	void findAdesioneById(Integer idAdesione, AsyncCallback<Adesioni> callback);
	void createAdesione(AsyncCallback<Adesioni> callback);
	void deleteAdesione(String codiceAdesione, AsyncCallback<Boolean> callback);
	
	void findRinnoviMassivi(Integer idPeriodico, AsyncCallback<List<RinnoviMassivi>> callback);
	void saveOrUpdateRinnoviMassiviList(List<RinnoviMassivi> rinnoviMassiviList,
			AsyncCallback<Boolean> callback);
	void deleteRinnovoMassivo(Integer idRinnovoMassivo, AsyncCallback<Boolean> callback);

}

package it.giunti.apg.client.services;

import java.util.Date;
import java.util.List;

import com.google.gwt.user.client.rpc.AsyncCallback;

import it.giunti.apg.shared.model.AliquoteIva;
import it.giunti.apg.shared.model.FileResources;
import it.giunti.apg.shared.model.Macroaree;
import it.giunti.apg.shared.model.Nazioni;
import it.giunti.apg.shared.model.Periodici;
import it.giunti.apg.shared.model.Professioni;
import it.giunti.apg.shared.model.Province;
import it.giunti.apg.shared.model.Societa;
import it.giunti.apg.shared.model.TipiDisdetta;
import it.giunti.apg.shared.model.TitoliStudio;

public interface LookupServiceAsync {
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

}

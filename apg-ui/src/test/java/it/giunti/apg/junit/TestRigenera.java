/**
 * 
 */
package it.giunti.apg.junit;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.Date;

import org.junit.Test;

import it.giunti.apg.server.services.AbbonamentiServiceImpl;
import it.giunti.apg.shared.model.Fascicoli6;
import it.giunti.apg.shared.model.IstanzeAbbonamenti;

/**
 * @author lmeoni
 *
 */
public class TestRigenera {
	
	@Test
	public void test() {
		AbbonamentiServiceImpl abbonamentiServiceImpl = new AbbonamentiServiceImpl();
		
		Integer idOldIstanza = 733709;
		//TODO trovare una istanza migliore
		String userId = "bridge";
		
		try {
			// Rigenera istanza senza persistenza
			IstanzeAbbonamenti newIstanzeAbbonamenti = abbonamentiServiceImpl.makeBasicRegeneration(idOldIstanza, userId);
			
			// l'operatore associa il fascicolo di fine e inizio e poi salva e viene effettuata la creazione degi arretrati
			
			
			// check booking instance
			assertTrue("checked data inizio ", newIstanzeAbbonamenti.getFascicoloInizio6().getDataInizio().before(new Date()) && 
					newIstanzeAbbonamenti.getFascicoloInizio6().getDataFine().after(new Date()));
			
			
			
//			// delete booking instance
//			Boolean deleteIstanza = abbonamentiServiceImpl.deleteIstanza(newIstanzeAbbonamenti.getId());
//			
//			// check booking instance		
//			assertEquals("checked deleted ", Boolean.TRUE, deleteIstanza);
//			
//			IstanzeAbbonamenti reloadOldInstance = abbonamentiServiceImpl.findIstanzeById(idOldIstanza);
//			
//			assertEquals("check if is the last of series", true, reloadOldInstance.getUltimaDellaSerie());
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		
	}
}

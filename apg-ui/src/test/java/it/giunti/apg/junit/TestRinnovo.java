/**
 * 
 */
package it.giunti.apg.junit;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import it.giunti.apg.server.services.AbbonamentiServiceImpl;
import it.giunti.apg.shared.model.IstanzeAbbonamenti;

/**
 * @author lmeoni
 *
 */
public class TestRinnovo {
	
	@Test
	public void test() {
		AbbonamentiServiceImpl abbonamentiServiceImpl = new AbbonamentiServiceImpl();
		
		Integer idOldIstanza = 733709;
		//TODO trovare una istanza migliore
		String userId = "bridge";
		
		try {
			// renew booking instance
			IstanzeAbbonamenti newIstanzeAbbonamenti = abbonamentiServiceImpl.makeBasicRenewal(idOldIstanza, userId);
			
			// check booking instance
			assertEquals("checked numero inizio", "373", newIstanzeAbbonamenti.getFascicoloInizio().getTitoloNumero());
			
			// delete booking instance
			Boolean deleteIstanza = abbonamentiServiceImpl.deleteIstanza(newIstanzeAbbonamenti.getId());
			
			// check booking instance		
			assertEquals("checked deleted ", Boolean.TRUE, deleteIstanza);
			
			IstanzeAbbonamenti reloadOldInstance = abbonamentiServiceImpl.findIstanzeById(idOldIstanza);
			
			assertEquals("check if is the last of series", true, reloadOldInstance.getUltimaDellaSerie());
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		
	}
}

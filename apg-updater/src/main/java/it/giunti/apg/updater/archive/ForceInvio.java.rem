package it.giunti.apg.updater.archive;

import it.giunti.apg.core.ServerConstants;
import it.giunti.apg.core.persistence.EvasioniFascicoliDao;
import it.giunti.apg.core.persistence.GenericDao;
import it.giunti.apg.core.persistence.SessionFactory;
import it.giunti.apg.shared.model.EvasioniFascicoli;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.text.ParseException;
import java.util.Date;

import org.hibernate.Session;
import org.hibernate.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ForceInvio {
	
	static private Logger LOG = LoggerFactory.getLogger(ForceInvio.class);
	
	public static void force(String[] args) {
		//param: fileFattureRegistrate
		String filePath = args[0];
		File fatRegFile= new File(filePath);
		if (!fatRegFile.exists()) LOG.error("File not found: "+filePath);
		Date fakeDate=null;
		try {
			fakeDate = ServerConstants.FORMAT_DAY.parse("01/01/1980");
		} catch (ParseException e1) {
			e1.printStackTrace();
		}
		EvasioniFascicoliDao efDao = new EvasioniFascicoliDao();
		
		Session ses = SessionFactory.getSession();
  		Transaction trn = ses.beginTransaction();
  		int count = 0;
  		try {
			FileReader reader = new FileReader(fatRegFile);
			BufferedReader br = new BufferedReader(reader);
			String line = null;
			do {
				line = br.readLine();
				if (line != null) {
					String num = line.trim();
					if (!num.equals("")) {
						Integer id = Integer.parseInt(num);
						EvasioniFascicoli ef = GenericDao.findById(ses, EvasioniFascicoli.class, id);
						if (ef != null) {
							ef.setDataInvio(fakeDate);
							efDao.save(ses, ef);
							count++;
						} else {
							System.out.println(num);
						}
					}
				}
			} while (line != null);
			br.close();
			reader.close();
			trn.commit();
			System.out.println(count);
	  	} catch (Exception e) {
			trn.rollback();
			LOG.error(e.getMessage(), e);
		} finally {
			ses.close();
		}
  		
	}
	
}

package it.giunti.apg.server.persistence;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;

import javax.persistence.Entity;

import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;

public class QueryFactory {

	//private static final Logger LOG = LoggerFactory.getLogger(QueryFactory.class);
	
	private String qs;
	private Session ses;
	private ArrayList<String> wheres;
	private ArrayList<String> params;
	private ArrayList<Object> values;
	private ArrayList<String> paramList;
	private ArrayList<Object[]> valueList;
	private ArrayList<String> orderBy;
	private ArrayList<String> groupBy;
	private Integer pagingSize;
	private Integer pagingOffset;
	
	/**
	 * Genera delle query prendendo in input il comando di base e aggiungendo where e orderBy
	 * @param queryCommand è la stringa "select o from Qualcosa o" senza la parte di where
	 */
	public QueryFactory(Session session, String queryCommand)
			throws HibernateException {
		if (session==null) {
			throw new HibernateException("l'entityManager è null");
		}
		this.ses= session;
		this.qs = queryCommand;
		//Se non c'è aggiunge una spazio in fondo
		if (qs.charAt(qs.length()-1) != ' ') {
			qs += " ";
		}
		wheres= new ArrayList<String>();
		params= new ArrayList<String>();
		values= new ArrayList<Object>();
		paramList = new ArrayList<String>();
		valueList = new ArrayList<Object[]>();
		orderBy= new ArrayList<String>();
		groupBy= new ArrayList<String>();
		pagingSize=null;
		pagingOffset=null;
	}
	
	/**
	 * Aggiunge una condizione where alla query.<br>
	 * <b>whereCondition</b> è la semplice stringa nella forma parametrica "nomeCampo = :param"
	 * @param whereCondition
	 */
	public void addWhere(String whereCondition) {
		wheres.add(whereCondition);
	}
	
	/**
	 * Aggiunge il valore da associare ad un parametro.<br>
	 * <b>parameterName</b> stringa che identifica il parametro (senza : iniziale)
	 * <b>value</b> valore Serializable da inserire come parametro
	 * @param parameterName
	 * @param value
	 */
	public void addParam(String parameterName, Serializable value) {
		params.add(parameterName);
		values.add(value);
	}
	public void addTimestampParam(String parameterName, Date value) {
		params.add(parameterName);
		values.add((Long)value.getTime());
	}
	public void addParamList(String parameterName, Object[] value) {
		paramList.add(parameterName);
		valueList.add(value);
	}
	
	/**
	 * Aggiunge alla query le condizioni di ordinamento.<br>
	 * <b>fieldName</b> Nella forma "nomeCampo asc" o "nomecampo desc"
	 * @param fieldName
	 */
	public void addOrder(String fieldNameAndOrder) {
		if (fieldNameAndOrder!=null) {
			orderBy.add(fieldNameAndOrder);
		}
	}
	
	/**
	 * Aggiunge alla query le condizioni di raggruppamento.<br>
	 * <b>fieldName</b> Nella forma "nomeCampo".
	 * @param fieldName
	 */
	public void addGroup(String groupingFieldName) {
		if (groupingFieldName!=null) {
			groupBy.add(groupingFieldName);
		}
	}
	
	public void setPaging(Integer offset, Integer size) {
		pagingOffset=offset;
		pagingSize=size;
	}
	
	public Query getQuery() throws HibernateException {
		if (wheres.size()>0) {
			qs += "where " + wheresToString()+" ";
		}
		if (groupBy.size()>0) {
			qs += "group by " + groupByToString()+" ";
		}
		if (orderBy.size()>0) {
			qs += "order by " + orderByToString()+" ";
		}
		Query q = ses.createQuery(qs);
		for (int i=0; i<params.size(); i++) {
			if (values.get(i)==null) {
				throw new HibernateException("il parametro :"+params.get(i)+" della query è null");
			}
			if (values.get(i) instanceof Date ) {
				q.setDate(params.get(i), (Date)values.get(i));
			}
			if (values.get(i) instanceof Long ) {
				q.setTimestamp(params.get(i), new Date((Long)values.get(i)));
			}
			if (values.get(i) instanceof String ) {
				q.setString(params.get(i), (String)values.get(i));
			}
			if (values.get(i) instanceof Integer ) {
				q.setInteger(params.get(i), (Integer)values.get(i));
			}
			if (values.get(i) instanceof Short ) {
				q.setShort(params.get(i), (Short)values.get(i));
			}
			if (values.get(i) instanceof Double ) {
				q.setDouble(params.get(i), (Double)values.get(i));
			}
			if (values.get(i) instanceof Byte ) {
				q.setByte(params.get(i), (Byte)values.get(i));
			}
			if (values.get(i) instanceof Boolean ) {
				q.setBoolean(params.get(i), (Boolean)values.get(i));
			}
			Entity e = values.get(i).getClass().getAnnotation(Entity.class);
			if ( e!=null ) {
				q.setEntity(params.get(i), values.get(i));
			}
		}
		//Per condizioni su relazioni molti a molti
		for (int i=0; i<paramList.size(); i++) {
			q.setParameterList(paramList.get(i), valueList.get(i));
		}
		//Pagination
		if ((pagingSize != null) && (pagingOffset != null)) {
			q.setFirstResult(pagingOffset);
            q.setMaxResults(pagingSize);

		}
		//LOG.debug("QueryFactory.getQuery: "+qs);
		return q;
	}
	
	public int getConditionsCount() {
		return wheres.size();
	}
	
	private String wheresToString() {
		String result = new String();
		for (int i=0; i<wheres.size(); i++) {
			result += "("+wheres.get(i)+")";
			if ((i+1)<wheres.size()) {
				//non è l'ultimo elemento
				result += " and ";
			} else {
				result += " ";
			}
		}
		return result;
	}
	
	private String orderByToString() {
		String result = new String();
		for (int i=0; i<orderBy.size(); i++) {
			result += orderBy.get(i);
			if ((i+1)<orderBy.size()) {
				//non è l'ultimo elemento
				result += ", ";
			} else {
				result += " ";
			}
		}
		return result;
	}
	
	private String groupByToString() {
		String result = new String();
		for (int i=0; i<groupBy.size(); i++) {
			result += groupBy.get(i);
			if ((i+1)<groupBy.size()) {
				//non è l'ultimo elemento
				result += ", ";
			} else {
				result += " ";
			}
		}
		return result;
	}
	
	public String getDebugDescription() {
		String result = "Query: "+getQuery()+" \r\nParams: ";
		for (int i=0; i<params.size(); i++) {
			result += params.get(i)+"=\""+values.get(i)+"\" ";
		}
		return result;
	}
}
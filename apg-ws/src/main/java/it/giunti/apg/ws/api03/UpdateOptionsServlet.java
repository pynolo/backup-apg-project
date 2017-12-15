package it.giunti.apg.ws.api03;

import java.io.IOException;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/*@WebServlet(Constants.PATTERN_API01+Constants.PATTERN_UPDATE_SUBSCRIPTION)*/
public class UpdateOptionsServlet extends ApiServlet {
	private static final long serialVersionUID = 5818394628867859745L;
	//private static final String FUNCTION_NAME = Constants.PATTERN_UPDATE_SUBSCRIPTION;
	//private static final Logger LOG = LoggerFactory.getLogger(UpdateSubscriptionServlet.class);

	/*example testing url:
	 http://127.0.0.1:8888/api01/create_subscription?access_key=1234&id_magazine=A&id_offering=0101EB&id_customer_recipient=6090P5&id_customer_payer=1090P0&options=A01A;A01B&quantity=2&cm_first_issue=X1601A&cm_last_issue=X1710A&id_payment_type=CCR&payment_amount=70.50&payment_date=2016-08-23&payment_note=ma%20vafagulo
	 */
	/**
     * @see HttpServlet#HttpServlet()
     */
    public UpdateOptionsServlet() {
    	super();
    	//LOG.info(FUNCTION_NAME+" started");
    }

	@Override
	public void init() throws ServletException {
		super.init();
	}

	@Override
	public void init(ServletConfig config) throws ServletException {
		super.init(config);
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
    @Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doGet(request, response);
	}
    
	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
    @Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		UpdateSubscriptionOptionsServlet servlet = new UpdateSubscriptionOptionsServlet();
		servlet.doGet(request, response);
	}

	
}

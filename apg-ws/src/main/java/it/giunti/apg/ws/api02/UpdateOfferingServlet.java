package it.giunti.apg.ws.api02;

import java.io.IOException;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/*@WebServlet(Constants.PATTERN_API01+Constants.PATTERN_UPDATE_OFFERING)*/
public class UpdateOfferingServlet extends ApiServlet {
	private static final long serialVersionUID = 6040175449932525005L;
	private static final String FUNCTION_NAME = Constants.PATTERN_UPDATE_OFFERING;
	private static final Logger LOG = LoggerFactory.getLogger(UpdateOfferingServlet.class);

	/*example testing url:
	 http://127.0.0.1:8888/api01/update_offering?access_key=1234&id_offering=0101EB
	 */
	/**
     * @see HttpServlet#HttpServlet()
     */
    public UpdateOfferingServlet() {
        super();
        LOG.info(FUNCTION_NAME+" started");
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
		UpdateSubscriptionOfferingServlet servlet = new UpdateSubscriptionOfferingServlet();
		servlet.doGet(request, response);
	}
	
}

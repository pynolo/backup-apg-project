package it.giunti.apg.client;

import it.giunti.apg.shared.AppConstants;

import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.i18n.client.NumberFormat;
import com.google.gwt.user.datepicker.client.DateBox;

public class ClientConstants {
	
	public static final String MANDATORY = "<span class=\"label-small-super\">&Oslash;</span>";//&oslash; \u2020
	public static final String SPAN_STOP = "</span>";
	public static final String SPAN_SMALL_START = "<span class=\"label-small-caps\">";
	
	public static final String[] MESI = {"", "gennaio", "febbraio", "marzo", "aprile", "maggio",
		"giugno", "luglio", "agosto", "settembre", "ottobre", "novembre", "dicembre"};// da 1 a 12
	
	public static final String APG_DEFAULT_TITLE = "APG";
	
	//CLIENT DAFAULTS
	public static final long LOGIN_EXPIRATION_TIME = (1000*60*60) * 96; //96 hours = 4 days;
	public static final int COOKIE_EXPIRATION_DAYS = 15;
	
	//FORMATS
	public static final DateTimeFormat FORMAT_DATETIME = DateTimeFormat.getFormat(AppConstants.PATTERN_DATETIME);
	public static final DateTimeFormat FORMAT_DAY = DateTimeFormat.getFormat(AppConstants.PATTERN_DAY);
	public static final DateTimeFormat FORMAT_DAY_SQL = DateTimeFormat.getFormat(AppConstants.PATTERN_DAY_SQL);
	public static final DateBox.Format BOX_FORMAT_DAY = new DateBox.DefaultFormat(FORMAT_DAY);
	public static final DateTimeFormat FORMAT_MONTH = DateTimeFormat.getFormat(AppConstants.PATTERN_DAY);
	public static final DateBox.Format BOX_FORMAT_MONTH = new DateBox.DefaultFormat(FORMAT_DAY);
	public static final DateTimeFormat FORMAT_YEAR = DateTimeFormat.getFormat("yyyy");
	public static final DateTimeFormat FORMAT_TIME = DateTimeFormat.getFormat(AppConstants.PATTERN_TIME);
	public static final DateBox.Format BOX_FORMAT_TIME = new DateBox.DefaultFormat(FORMAT_TIME);
	public static final NumberFormat FORMAT_CURRENCY = NumberFormat.getFormat(AppConstants.PATTERN_CURRENCY);
	public static final NumberFormat FORMAT_INTEGER = NumberFormat.getFormat(AppConstants.PATTERN_INTEGER);
	
	//Icons
	public static final String ICON_LOADING_BIG = "<img src='img/chat_loading.gif' style='vertical-align:middle;border:none;' title='In corso...' />";
	public static final Integer ICON_LOADING_WIDTH = 121;
	public static final Integer ICON_LOADING_HEIGHT = 23;
	public static final String ICON_LOADING_SMALL = "<img src='img/ajax-loader-small.gif' style='vertical-align:middle;border:none;' title='In corso...' />";
	public static final String ICON_MAGNIFIER = "<i class='fa fa-search'></i>";
	public static final String ICON_CARTACEO = "<i class='fa fa-book'></i>";
	public static final String ICON_APP = "<i class='fa fa-android'></i>";
	public static final String ICON_OPZIONE = "<i class='fa fa-cube'></i>";
	public static final String ICON_OPZIONI = "<i class='fa fa-cubes'></i>";
	public static final String ICON_SAVE = "<i class='fa fa-download'></i>";
	public static final String ICON_MERGE = "<i class='fa fa-compress'></i>";
	public static final String ICON_EDIT = "<span title='Modifica' ><i class='fa fa-pencil'></i></span>";//"<i class='fa fa-pencil-square-o'></i>";
	public static final String ICON_DELETE = "<span title='Elimina' ><i class='fa fa-trash'></i></span>";
	public static final String ICON_ADD = "<i class='fa fa-plus-square'></i>";
	public static final String ICON_PLUS = "<i class='fa fa-plus'></i>";
	public static final String ICON_IMPORTANT = "<i class='fa fa-exclamation-circle text-warning'></i>";//"<img src='img/icon16/emblem-important.png' style='vertical-align:middle;border:none;' />";
	public static final String ICON_WARNING = "<i class='fa fa-exclamation-triangle text-danger'></i>";
	public static final String ICON_AMBULANCE = "<i class='fa fa-ambulance'></i>";
	public static final String ICON_USER = "<i class='fa fa-user'></i>";
	public static final String ICON_USER_SUPER = "<img src='img/god.png' style='vertical-align:middle;border:none;' title='Amministratore' />";
	public static final String ICON_USER_ADMIN = "<img src='img/giglio.png' style='vertical-align:middle;border:none;' title='Amministratore' />";
	public static final String ICON_USER_EDITOR = ICON_USER;
	public static final String ICON_USER_OPERATOR = "<i class='fa fa-phone'></i>";
	public static final String ICON_ANAG_PRIVATO = "<span title='Privato' ><i class='fa fa-male'></i></span>";
	public static final String ICON_ANAG_SOCIETA = "<span title='Istituto/Societa' ><i class='fa fa-university'></i></span>";
	public static final String ICON_RINNOVA = "<span title='Rinnova' ><i class='fa fa-refresh'></i></span>";
	public static final String ICON_RIGENERA = "<span title='Rigenera' ><i class='fa fa-retweet'></i></span>";
	public static final String ICON_CUSTOMIZE = "<i class='fa fa-cutlery' ></i>";
	public static final String ICON_EMAIL = "<i class='fa fa-envelope'></i>";//"<img src='img/icon22/internet-mail.png' style='vertical-align:middle;border:none;' title='Email' />";
	public static final String ICON_ARROW = "<i class='fa fa-arrow-right'></i>";
	public static final String ICON_EURO = "<i class='fa fa-eur'></i>";
	public static final String ICON_LIGHTBULB = "<i class='fa fa-lightbulb-o' aria-hidden='true'></i>";
	public static final String ICON_MONEY = "<i class='fa fa-money' aria-hidden='true'></i>";
	
	public static final String ICON_USERS = "<i class='fa fa-users'></i>";
	public static final String ICON_USER_NEW =	"<i class='fa fa-user-plus'></i>";
	public static final String ICON_CLOCK = "<i class='fa fa-clock-o'></i>";
	public static final String ICON_DATABASE = "<i class='fa fa-database'></i>";
	public static final String ICON_PIECHART = "<i class='fa fa-pie-chart'></i>";
	public static final String ICON_WRENCH = "<i class='fa fa-wrench'></i>";
	public static final String ICON_LOG = "<i class='fa fa-list-ol'></i>";
	public static final String ICON_DANGER = "<i class='fa fa-exclamation-triangle'></i>";
	public static final String ICON_ANNOUNCE = "<i class='fa fa-bullhorn'></i>";
	public static final String ICON_QUERY = "<i class='fa fa-eye'></i>";
	public static final String ICON_HAND_LEFT = "<i class='fa fa-hand-o-left'></i>";
	public static final String ICON_HAND_RIGHT = "<i class='fa fa-hand-o-right'></i>";
	public static final String ICON_CHECK = "<i class='fa fa-check-square-o'></i>";
	public static final String ICON_FATTURA_DIFFERITO = "<span title='Fattura a pagamento differito'><i class='fa fa-file-text-o'></i></span>";
	public static final String ICON_FATTURA_CORRISPETTIVO = "<span title='Fattura da corrispettivo'><i class='fa fa-file-pdf-o'></i></span>";
	public static final String ICON_FATTURA_RIMBORSO = "<span title='Nota di credito'><i class='fa fa-reply'></i></span>";
	public static final String ICON_OMAGGIO = "<span title='Omaggio'><i class='fa fa-gift'></i></span>";
	
	public static final String ICON_MINI_BLOCCATO = "<i class='fa fa-bolt text-danger'></i>";
	public static final String ICON_MINI_DISDETTA = "<i class='fa fa-scissors text-danger'></i>";
	public static final String ICON_MINI_SCADUTO= "<i class='fa fa-calendar text-danger'></i>";
	public static final String ICON_MINI_FUTURO = "<i class='fa fa-calendar text-info'></i>";
	public static final String ICON_MINI_IN_CORSO = "<i class='fa fa-calendar text-success'></i>";
	public static final String ICON_MINI_OMAGGIO = "<i class='fa fa-gift text-success'></i>";
	public static final String ICON_MINI_FATTURAZIONE = "<i class='fa fa-file-text text-success'></i>";
	public static final String ICON_MINI_PAGATO = "<i class='fa fa-eur text-success'></i>";
	public static final String ICON_MINI_DA_PAGARE = "<i class='fa fa-eur text-danger'></i>";
	
	//LABELS
	public static final String LABEL_EMPTY_RESULT = "<i>Nessun risultato</i>";
	public static final String LABEL_LOADING = ICON_LOADING_SMALL+" <i>caricamento in corso...</i>";
	
	//COOKIE
	public static final String COOKIE_USERNAME = "rgrw4c4gidlds52gfd3";
	public static final String COOKIE_PASSWORD = "854njsc62odhg89gj3q";
	public static final String COOKIE_VERSION = "apgVersion";
	public static final String COOKIE_LAST_LOGIN = "lastLogin";
	public static final String COOKIE_LAST_PERIODICO = "idPeriodico";
	public static final String COOKIE_LAST_SOCIETA = "idSocieta";
	public static final String COOKIE_LAYOUT = "layout";
	public static final String COOKIE_LAST_ID_MAINTENANCE = "idMaintenance";
}

package automation.scripts;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import java.util.TreeSet;

import org.testng.IReporter;
import org.testng.IResultMap;
import org.testng.ISuite;
import org.testng.ISuiteResult;
import org.testng.ITestContext;
import org.testng.ITestResult;
import org.testng.xml.XmlSuite;

public class ReportGenerator implements IReporter {

	// public static String testResult;
	// private final NumberFormat formatter = new DecimalFormat("#,##0.0");
	private PrintWriter out;

	private String timeStamp;
	private String contextPath;

	public String getContextPath() {
		return contextPath;
	}

	public void setContextPath(String contextPath) {
		this.contextPath = contextPath;
	}

	public String getTimeStamp() {
		return timeStamp;
	}

	public void setTimeStamp(String timeStamp) {
		this.timeStamp = timeStamp;
	}

	@Override
	public void generateReport(List<XmlSuite> xmlSuite, List<ISuite> suites,
			String outputDir) {

		for (ISuite suite : suites) {

			try {

				out = createWriter(outputDir, suite.getName() + ".html");
			} catch (Exception e) {
			}
			generateSuiteSummaryReport(suite);

			out.flush();
			out.close();
		}

	}

	public void generateSuiteSummaryReport(ISuite suite) {

		startHTML();

		Map<String, ISuiteResult> tests = suite.getResults();

		TreeSet<Map.Entry<String, ISuiteResult>> set = new TreeSet<Map.Entry<String, ISuiteResult>>(
				tests.entrySet());

		for (Entry<String, ISuiteResult> entry : set) {

			String suiteName = entry.getKey();
			ISuiteResult suiteResult = entry.getValue();

			ITestContext context = suiteResult.getTestContext();

			// IResultMap skippedConfigs = context.getSkippedConfigurations();
			IResultMap failedConfigs = context.getFailedConfigurations();
			IResultMap successTests = context.getPassedTests();
			IResultMap failedTests = context.getFailedTests();
			IResultMap skippedTests = context.getSkippedTests();
			IResultMap failedSuccess = context
					.getFailedButWithinSuccessPercentageTests();

			int[] count = new int[4];

			count[0] = failedTests.size();
			count[1] = successTests.size();
			count[2] = skippedTests.size();
			count[3] = count[0] + count[1] + count[2];

			createSummary(suiteName, count);

			// TreeSet<ITestResult> skippedConfigResults = new
			// TreeSet<ITestResult>(skippedConfigs.getAllResults());
			TreeSet<ITestResult> failedConfigResults = new TreeSet<ITestResult>(
					failedConfigs.getAllResults());
			TreeSet<ITestResult> failedResults = new TreeSet<ITestResult>(
					failedTests.getAllResults());
			TreeSet<ITestResult> successResults = new TreeSet<ITestResult>(
					successTests.getAllResults());
			TreeSet<ITestResult> skippedResults = new TreeSet<ITestResult>(
					skippedTests.getAllResults());

			TreeSet<ITestResult> fsResults = new TreeSet<ITestResult>(
					failedSuccess.getAllResults());

			if (!failedConfigResults.isEmpty()) {
				createTitle("configFail", "Failed Configurations ");
				startTable();
				createConfigFailTableHeader();
				parseConfigFailTableRow(failedConfigResults, "configFail");
				endTable();
			}

			/*
			 * if (!skippedConfigResults.isEmpty()) { createTitle("skipped",
			 * "Skipped Configurations"); startTable(); createTableHeader();
			 * parseTableRow(skippedConfigResults, "skipped"); endTable(); }
			 */
			if (!failedResults.isEmpty()) {
				createTitle("failed", "Failed Test Cases");
				startTable();
				createTableHeader();
				parseTableRow(failedResults, "failed");
				endTable();
			}

			if (!fsResults.isEmpty()) {
				createTitle("failed", "Failed with Some Success Percentage");
				startTable();
				createTableHeader();
				parseTableRow(fsResults, "failed");
				endTable();
			}

			if (!skippedResults.isEmpty()) {
				createTitle("skipped", "Skipped Test Cases");
				startTable();
				createTableHeader();
				parseTableRow(skippedResults, "skipped");
				endTable();
			}

			if (!successResults.isEmpty()) {
				createTitle("success", "Passed Test Cases");
				startTable();
				createSuccessTableHeader();
				parseSuccessTableRow(successResults, "success");
				endTable();
			}

		}

		endHTML();
	}

	protected void parseTableRow(TreeSet<ITestResult> testResults,
			String cssClass) {

		for (ITestResult iTestResult : testResults) {

			String[] rows = new String[6];

			Object[] params = iTestResult.getParameters();
			for (Object p : params) {
				rows[0] = p + " ";
			}

			try {
				rows[1] = iTestResult.getThrowable().getMessage();
			} catch (Exception e) {
			}

			rows[2] = getDate(iTestResult.getStartMillis());

			rows[3] = String.valueOf("   "
					+ ((iTestResult.getEndMillis() - iTestResult
							.getStartMillis())) + "  ms");
			try {
				StackTraceElement[] trace = iTestResult.getThrowable()
						.getStackTrace();
				rows[4] = "";
				for (StackTraceElement se : trace) {
					rows[4] += se + "\n";
				}

				rows[5] = contextPath + "/tester/screenshots.htm?time="
						+ timeStamp + "&testcase=" + rows[0];

			} catch (Exception e) {
			}

			createTableRow(cssClass, rows);
		}

	}

	protected void parseConfigFailTableRow(TreeSet<ITestResult> testResults,
			String cssClass) {

		for (ITestResult iTestResult : testResults) {

			String[] rows = new String[4];

			try {
				rows[0] = iTestResult.getThrowable().getMessage();
			} catch (Exception e) {
			}

			rows[1] = getDate(iTestResult.getStartMillis());

			rows[2] = String.valueOf("   "
					+ ((iTestResult.getEndMillis() - iTestResult
							.getStartMillis())) + "  ms");
			try {
				StackTraceElement[] trace = iTestResult.getThrowable()
						.getStackTrace();
				rows[3] = "";
				for (StackTraceElement se : trace) {
					rows[3] += se + "\n";
				}

			} catch (Exception e) {
			}

			createConfigFailTableRow(cssClass, rows);
		}

	}

	protected void parseSuccessTableRow(TreeSet<ITestResult> testResults,
			String cssClass) {

		for (ITestResult iTestResult : testResults) {

			String[] rows = new String[3];

			Object[] params = iTestResult.getParameters();
			for (Object p : params) {
				rows[0] = p + " ";
			}

			rows[1] = getDate(iTestResult.getStartMillis());

			rows[2] = String.valueOf("   "
					+ ((iTestResult.getEndMillis() - iTestResult
							.getStartMillis())) + "  ms");
			createSuccessTableRow(cssClass, rows);
		}

	}

	protected void createCssContent() {

		out.println("<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.1//EN http://www.w3.org/TR/xhtml11/DTD/xhtml11.dtd\">");

		out.println("<html><head>" + "<style>" + "body {"
				+ "font-family: Verdana;" + "font-size: 10px;"
				+ "text-transform: none;" + "background-color: #FFFFFF;" + "}" +

				".black {" + "text-align: center;" + "line-height: 20px;"
				+ "color: #000000;" + "}" +

				".configFail {" + "text-align: center;" + "line-height: 20px;"
				+ "color: #C85A17;" + "}" +

				".failed {" + "text-align: center;" + "line-height: 20px;"
				+ "color: #FF0000;" + "}" +

				".success {" + "text-align: center;" + "line-height: 20px;"
				+ "color: #008000;" + "}" +

				".skipped {" + "text-align: center;" + "line-height: 20px;"
				+ "color: #A4A4A4;" + "}" +

				".stable {" + "border: 2px #A4A4A4 solid;"
				+ "line-height: 25px;" + "background-color: #99EFF8FB;" + "}" +

				".stable th {" + "border-right: 2px #A4A4A4 solid;"
				+ "border-bottom: 2px #A4A4A4 solid;" + "font-weight: bold;"
				+ "background-color: #F2FBEF;" + "}" +

				".stable td {" + "border-right: 2px #A4A4A4 solid;"
				+ "border-bottom: 2px #A4A4A4 solid;" + "font-weight: bold;"
				+ "text-align: center;" + "}" +

				".trace {" + "text-decoration: none;" + "}" + "</style>"
				+ "<script src='" + contextPath + "/js/custom.js' "
				+ " type='text/javascript'></script>" + "</head>");

	}

	protected void createTitle(String cssClass, String title) {
		out.append("<h3 class='" + cssClass + "' style='text-align: left;'>"
				+ title + "</h3>");
	}

	public void startTable() {
		out.append("<table class='stable' width='100%' cellspacing='0' cellpadding='0'	align='center'>");
	}

	protected void createTableHeader() {

		out.append("<tr>" + "<th>Test Case </th>" + "<th>Message</th>"
				+ "<th style='width: 300px'>Start Time</th>"
				+ "<th>Execution Time</th>" + "<th>StackTrace</th>"
				+ "<th>Screens</th>" + "</tr>");
	}

	protected void createConfigFailTableHeader() {

		out.append("<tr>" + "<th>Message</th>"
				+ "<th style='width: 300px'>Start Time</th>"
				+ "<th>Execution Time</th>" + "<th>StackTrace</th>" + "</tr>");
	}

	protected void createSuccessTableHeader() {

		out.append("<tr>" + "<th>Test Case </th>"
				+ "<th style='width: 300px'>Start Time</th>"
				+ "<th>Execution Time</th>" + "</tr>");
	}

	protected void createTableRow(String cssClass, String[] data) {

		out.append("<tr>" + "<td class='" + cssClass
				+ "'  style='text-align:right;padding-right:40px;'>" + data[0]
				+ "</td>" + "<td class='" + cssClass + "'>" + data[1] + "</td>"
				+ "<td>" + data[2] + "</td>" + "<td>" + data[3] + "</td>"
				+ "<td><a href='#' class='trace' title='" + data[4]
				+ "'>Hover to view</a></td>" + "<td><a href=javascript:openImg('" + data[5].trim()
				+ "')> <img src='"+contextPath+"/css/images/attachment.png'/></a> </td>" + "</tr>");
	}

	protected void createConfigFailTableRow(String cssClass, String[] data) {

		out.append("<tr>" + "<td class='" + cssClass + "'>" + data[0] + "</td>"
				+ "<td>" + data[1] + "</td>" + "<td>" + data[2] + "</td>"
				+ "<td><a href='#' class='trace' title='" + data[3]
				+ "'>Hover to view</a></td>" + "</tr>");
	}

	protected void createSuccessTableRow(String cssClass, String[] data) {

		out.append("<tr>" + "<td class='" + cssClass
				+ "' style='text-align:right;padding-right:40px;'>" + data[0]
				+ "</td>" + "<td>" + data[1] + "</td>" + "<td>" + data[2]
				+ "</td>" + "</tr>");
	}

	public void endTable() {
		out.append("</table>");
	}

	protected void startHTML() {
		createCssContent();
		out.append("<body>");
	}

	protected void endHTML() {
		out.append("</html>");
	}

	protected void createSummary(String suiteName, int[] results) {

		out.append("	<table class='stable' style='min-width:520px;' cellspacing='0' cellpadding='0' align='center'>"
				+ "<tr><td colspan='2'></td>  </tr>"
				+ "<tr>"
				+ "<th style='min-width=180px;'>Suite Name</th>"
				+ "<td class='black'>"
				+ suiteName
				+ "</td>"
				+ "</tr>"
				+ "<tr>"
				+ "<th>Failed Test Cases</th>"
				+ "<td class='failed'>"
				+ results[0]
				+ "</td>"
				+ "</tr>"
				+

				"<tr>"
				+ "<th>Passed Test Cases</th>"
				+ "<td class='success'>"
				+ results[1]
				+ "</td>"
				+ "</tr>"
				+

				"<tr>"
				+ "<th>Skipped Test Cases</th>"
				+ "<td class='skipped'>"
				+ results[2]
				+ "</td>"
				+ "</tr>"
				+ "<tr>"
				+ "<th>Total Test Cases</th>"
				+ "<td class='black'>"
				+ results[3] + "</td>" + "</tr>" + "</table>");

	}

	protected PrintWriter createWriter(String outdir, String fileName)
			throws IOException {
		return new PrintWriter(new BufferedWriter(new FileWriter(new File(
				outdir, fileName))));
	}

	public String getDate(long time) {
		SimpleDateFormat sdfDate = new SimpleDateFormat(
				"yyyy-MMM-dd HH:mm:ss z");// dd/MM/yyyy
		return sdfDate.format(new Date(time));

	}

}

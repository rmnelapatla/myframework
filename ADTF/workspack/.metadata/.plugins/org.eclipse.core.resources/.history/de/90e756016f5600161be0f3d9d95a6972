package com.adaequare.testng.adtf.parser;

import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.testng.xml.XmlClass;
import org.testng.xml.XmlSuite;
import org.testng.xml.XmlTest;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.adaequare.testng.adtf.filters.HTMLFilter;
import com.adaequare.testng.adtf.filters.UserFilter;
import com.adaequare.testng.adtf.filters.XMLFilter;
import com.adaequare.testng.adtf.testng.beans.Configuration;
import com.adaequare.testng.adtf.testng.beans.Reports;
import com.adaequare.testng.adtf.testng.beans.TestSteps;
import com.adaequare.testng.adtf.testng.beans.TestSuite;
import com.adaequare.testng.adtf.util.AdtfDateUtil;

public class XMLParser {

	public Logger logger = Logger.getRootLogger();
	@Autowired
	private DocumentBuilderFactory documentBuilderFactory;

	@Autowired
	private TransformerFactory transformerFactory;

	DocumentBuilder builder;
	Document document;
	Transformer transformer;

	public void setTransformerFactory(TransformerFactory transformerFactory) {
		this.transformerFactory = transformerFactory;
	}

	public TransformerFactory getTransformerFactory() {
		return transformerFactory;
	}

	public void setDocumentBuilderFactory(
			DocumentBuilderFactory documentBuilderFactory) {
		this.documentBuilderFactory = documentBuilderFactory;
	}

	public DocumentBuilderFactory getDocumentBuilderFactory() {
		return documentBuilderFactory;
	}

	public int createModule(String path, String project, String moduleName) {
		try {

			builder = documentBuilderFactory.newDocumentBuilder();
			document = builder.newDocument();

			File file = new File(path + File.separator + "data"
					+ File.separator + "projects" + File.separator + project
					+ File.separator + "test-input" + File.separator
					+ "Categories.xml");

			if (!file.exists()) {
				Element rootElement = document.createElement("Categories");
				document.appendChild(rootElement);
				Element cat = document.createElement("cat");
				cat.setTextContent(moduleName);
				rootElement.appendChild(cat);
			} else {

				if (getCategories(path, project).contains(moduleName)) {
					return 1;// duplicate
				}

				document = builder.parse(file);
				document.getDocumentElement().normalize();

				Element cat = document.createElement("cat");
				cat.setTextContent(moduleName);

				document.getDocumentElement().appendChild(cat);

			}

			transformer = transformerFactory.newTransformer();
			DOMSource source = new DOMSource(document);
			StreamResult result = new StreamResult(file);
			transformer.transform(source, result);

			// creating required folder and xml files
			createModuleFolder(path, project, moduleName);

		} catch (Exception e) {

			e.printStackTrace();
			return -1;
		}
		return 0;
	}

	public void createProjectFolder(String path, String projectName) {

		File projectDir = new File(path + File.separator + "data"
				+ File.separator + "projects" + File.separator + projectName);

		if (!projectDir.exists()) {
			projectDir.mkdir();
		}
		File configDir = new File(path + File.separator + "data"
				+ File.separator + "projects" + File.separator + projectName
				+ File.separator + "configs");
		if (!configDir.exists()) {
			configDir.mkdir();
		}

		File testInput = new File(path + File.separator + "data"
				+ File.separator + "projects" + File.separator + projectName
				+ File.separator + "test-input");
		if (!testInput.exists()) {
			testInput.mkdir();
		}

		File testOutput = new File(path + File.separator + "data"
				+ File.separator + "projects" + File.separator + projectName
				+ File.separator + "test-output");
		if (!testOutput.exists()) {
			testOutput.mkdir();
		}

		File failedTestCases = new File(path + File.separator + "data"
				+ File.separator + "projects" + File.separator + projectName
				+ File.separator + "failed-tests");
		if (!failedTestCases.exists()) {
			failedTestCases.mkdir();
		}

		try {

			String xmlFilesPath = path + File.separator + "data"
					+ File.separator + "projects" + File.separator
					+ projectName + File.separator + "test-input";
			createObjectReferencesFile(xmlFilesPath);
			createMessagesFile(xmlFilesPath);
			createTestDetails(xmlFilesPath);
			createTestSteps(xmlFilesPath);

		} catch (Exception e) {
			logger.error(e.getMessage());
		}

	}

	public void createModuleFolder(String path, String project,
			String moduleName) {

		File moduleDir = new File(path + File.separator + "data"
				+ File.separator + "projects" + File.separator + project
				+ File.separator + "test-input" + File.separator + moduleName);

		if (!moduleDir.exists()) {
			moduleDir.mkdir();
		}

		try {

			String xmlFilesPath = path + File.separator + "data"
					+ File.separator + "projects" + File.separator + project
					+ File.separator + "test-input";

			createTestsToLoad(xmlFilesPath, moduleName);

		} catch (Exception e) {
			logger.error("  error" + e.getMessage());
		}

	}

	public void createFailedTestCase(String path, String project,
			String timeStamp, List<String> testCaseList) {
		try {

			String folderPath = path + File.separator + "data" + File.separator
					+ "projects" + File.separator + project + File.separator
					+ "failed-tests" + File.separator + timeStamp;
			File f = new File(folderPath);
			if (!f.exists()) {
				f.mkdirs();
			}

			File msgFile = new File(folderPath + File.separator
					+ "FailedTestCases.xml");

			builder = documentBuilderFactory.newDocumentBuilder();
			document = builder.newDocument();
			Element rootElement = document.createElement("TestCases");
			document.appendChild(rootElement);

			for (String string : testCaseList) {
				Element testElement = document.createElement("Test");
				testElement.setTextContent(string);
				rootElement.appendChild(testElement);
			}

			transformer = transformerFactory.newTransformer();
			DOMSource source = new DOMSource(document);
			StreamResult result = new StreamResult(msgFile);
			transformer.transform(source, result);

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public int getFailedTestCasesCount(String path, String project, String time) {
		try {

			String folderPath = path + File.separator + "data" + File.separator
					+ "projects" + File.separator + project + File.separator
					+ "failed-tests" + File.separator + time + File.separator
					+ "FailedTestCases.xml";
			File failedTestCaseFile = new File(folderPath);
			if (failedTestCaseFile.exists()) {

				builder = documentBuilderFactory.newDocumentBuilder();
				document = builder.parse(failedTestCaseFile);
				document.getDocumentElement().normalize();
				NodeList nodeList = document.getElementsByTagName("Test");
				return nodeList.getLength();
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

		return 0;
	}

	public List<String> getFailedTestCasesNames(String path, String project,
			String time) {
		try {

			String folderPath = path + File.separator + "data" + File.separator
					+ "projects" + File.separator + project + File.separator
					+ "failed-tests" + File.separator + time + File.separator
					+ "FailedTestCases.xml";
			File failedTestCaseFile = new File(folderPath);

			if (failedTestCaseFile.exists()) {
				List<String> testCaseList = new ArrayList<String>();

				builder = documentBuilderFactory.newDocumentBuilder();
				document = builder.parse(failedTestCaseFile);
				document.getDocumentElement().normalize();
				NodeList nodeList = document.getElementsByTagName("Test");

				for (int i = 0; i < nodeList.getLength(); i++) {
					testCaseList.add(nodeList.item(i).getTextContent());
				}

				return testCaseList;
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

		return Collections.emptyList();
	}

	public int removeModule(String path, String project, String module) {

		String file = path + File.separator + "data" + File.separator
				+ "projects" + File.separator + project + File.separator
				+ "test-input" + File.separator + "Categories.xml";

		int res = removeNode(file, "cat", "", module);
		return res;
	}

	public void createObjectReferencesFile(String path) throws Exception {

		File objFile = new File(path + File.separator + "ObjectReference.xml");
		if (!objFile.exists()) {
			builder = documentBuilderFactory.newDocumentBuilder();
			document = builder.newDocument();
			Element rootElement = document.createElement("Reference");
			document.appendChild(rootElement);
			transformer = transformerFactory.newTransformer();
			DOMSource source = new DOMSource(document);
			StreamResult result = new StreamResult(objFile);
			transformer.transform(source, result);
		}
	}

	public boolean deleteObjectReferencesFile(String path) throws Exception {
		File objFile = new File(path + File.separator + "ObjectReference.xml");
		if (objFile.exists())
			return objFile.delete();
		return false;
	}

	public int updateObjectReferecesFile(String path, String projectName,
			Map<String, String> objmap) {

		try {

			String file = path + File.separator + "data" + File.separator
					+ "projects" + File.separator + projectName
					+ File.separator + "test-input";
			File objFile = new File(file + File.separator
					+ "ObjectReference.xml");

			builder = documentBuilderFactory.newDocumentBuilder();

			if (objFile.exists()) {
				document = builder.parse(objFile);
				document.getDocumentElement().normalize();
			} else {
				document = builder.newDocument();
			}

			Element rootElement = document.getDocumentElement();

			Set<Map.Entry<String, String>> objSet = objmap.entrySet();

			for (Entry<String, String> entry : objSet) {
				Element element = document.createElement("RefValues");
				element.setAttribute("aName", entry.getKey());
				element.setAttribute("bObjRef", entry.getValue());

				rootElement.appendChild(element);
			}

			transformer = transformerFactory.newTransformer();
			transformer.setOutputProperty(OutputKeys.INDENT, "yes");
			DOMSource source = new DOMSource(document);
			StreamResult result = new StreamResult(objFile);

			transformer.transform(source, result);

			return 0;
		} catch (Exception e) {
			logger.error("  error" + e.getMessage());
			return -1;
		}

	}

	public int updateTestConfiguration(String path, String projectName,
			String configName, String hostName, String browser,
			String bVersion, String platformName, Integer port) {

		try {
			String file = path + File.separator + "data" + File.separator
					+ "projects" + File.separator + projectName
					+ File.separator + "configs";

			File configsFolder = new File(file);
			if (!configsFolder.exists()) {
				configsFolder.mkdir();
			}

			File configFile = new File(file + File.separator
					+ "TestConfiguration.xml");

			builder = documentBuilderFactory.newDocumentBuilder();
			Element rootElement = null;
			if (configFile.exists()) {
				document = builder.parse(configFile);
				document.getDocumentElement().normalize();
				rootElement = document.getDocumentElement();
			} else {
				document = builder.newDocument();
				rootElement = document.createElement("TestConfiguration");
				document.appendChild(rootElement);
			}

			Element testConfig = document.createElement("TestConfig");
			testConfig.setAttribute("name", configName);

			Element hostname = document.createElement("hostname");
			hostname.setTextContent(hostName);
			testConfig.appendChild(hostname);

			Element portnumber = document.createElement("portnumber");
			portnumber.setTextContent(String.valueOf(port));
			testConfig.appendChild(portnumber);

			Element browsertype = document.createElement("browsertype");
			browsertype.setTextContent(browser);
			testConfig.appendChild(browsertype);

			Element platform = document.createElement("platform");
			platform.setTextContent(platformName);
			testConfig.appendChild(platform);

			Element browserversion = document.createElement("browserversion");
			browserversion.setTextContent(bVersion);
			testConfig.appendChild(browserversion);

			rootElement.appendChild(testConfig);

			transformer = transformerFactory.newTransformer();
			transformer.setOutputProperty(OutputKeys.INDENT, "yes");
			DOMSource source = new DOMSource(document);
			StreamResult result = new StreamResult(configFile);

			transformer.transform(source, result);
			return 0;
		} catch (Exception e) {
			logger.error("  error" + e.getMessage());
			return -1;
		}

	}

	public Map<String, String> getObjectReferences(String path,
			String projectName) {
		Map<String, String> objMap = new LinkedHashMap<String, String>();
		String objName = "";
		try {
			String file = path + File.separator + "data" + File.separator
					+ "projects" + File.separator + projectName
					+ File.separator + "test-input";
			File objFile = new File(file + File.separator
					+ "ObjectReference.xml");

			String objVal = "";
			builder = documentBuilderFactory.newDocumentBuilder();
			document = builder.parse(objFile);
			document.getDocumentElement().normalize();
			NodeList nodeList = document.getElementsByTagName("RefValues");
			for (int i = 0; i < nodeList.getLength(); i++) {
				objName = nodeList.item(i).getAttributes()
						.getNamedItem("aName").getNodeValue();
				objVal = nodeList.item(i).getAttributes()
						.getNamedItem("bObjRef").getNodeValue();
				objMap.put(objName, objVal);
			}

		} catch (Exception e) {
			logger.error("  error" + e.getMessage());
		}

		return objMap;
	}

	public Map<String, String> getMessageInfo(String path, String projectName) {
		Map<String, String> msgMap = new HashMap<String, String>();

		try {
			String file = path + File.separator + "data" + File.separator
					+ "projects" + File.separator + projectName
					+ File.separator + "test-input";
			File msgFile = new File(file + File.separator + "Messages.xml");

			if (!msgFile.exists()) {
				createMessagesFile(file);
			}
			String msgName = "";
			String msgVal = "";
			builder = documentBuilderFactory.newDocumentBuilder();
			document = builder.parse(msgFile);
			document.getDocumentElement().normalize();
			NodeList nodeList = document.getElementsByTagName("Msg");
			for (int i = 0; i < nodeList.getLength(); i++) {

				msgName = nodeList.item(i).getAttributes().getNamedItem("name")
						.getNodeValue();
				msgVal = nodeList.item(i).getTextContent();

				msgMap.put(msgName, msgVal);
			}

		} catch (Exception e) {
			logger.error(e.getMessage());
		}

		return msgMap;
	}

	public Map<String, TestSuite> getTestSuites(String path, String projectName) {
		Map<String, TestSuite> suiteSet = new LinkedHashMap<String, TestSuite>();

		try {
			String file = path + File.separator + "data" + File.separator
					+ "projects" + File.separator + projectName
					+ File.separator + "configs";
			File objFile = new File(file + File.separator + "Suite.xml");
			if (objFile.exists()) {
				builder = documentBuilderFactory.newDocumentBuilder();
				document = builder.parse(objFile);
				document.getDocumentElement().normalize();
				NodeList nodeList = document.getElementsByTagName("Suite");
				TestSuite testSuite = null;
				for (int i = 0; i < nodeList.getLength(); i++) {
					testSuite = new TestSuite();

					NodeList childNode = nodeList.item(i).getChildNodes();
					for (int a = 0; a < childNode.getLength(); a++) {

						if ("urls".equalsIgnoreCase(childNode.item(a)
								.getNodeName())) {
							testSuite
									.setUrl(childNode.item(a).getTextContent());

						} else if ("cats".equalsIgnoreCase(childNode.item(a)
								.getNodeName())) {
							testSuite.setModule(childNode.item(a)
									.getTextContent());
						}
					}
					suiteSet.put(
							nodeList.item(i).getAttributes()
									.getNamedItem("name").getNodeValue(),
							testSuite);
				}
			}
		} catch (Exception e) {
			logger.error(e.getMessage());
		}

		return suiteSet;
	}

	public int removeTestSuite(String path, String projectName, String suiteName) {
		String file = path + File.separator + "data" + File.separator
				+ "projects" + File.separator + projectName + File.separator
				+ "configs";
		// need to remove folder
		int flag = removeNode(file + File.separator + "Suite.xml", "Suite",
				"name", suiteName);
		if (flag == 0) {
			File suiteFolder = new File(file + File.separator + suiteName);
			if (suiteFolder.exists()) {
				suiteFolder.delete();
			}
			return 0;
		} else
			return -1;
	}

	public Map<String, Configuration> getConfigurations(String path,
			String projectName) {
		Map<String, Configuration> suiteSet = new LinkedHashMap<String, Configuration>();

		try {
			String file = path + File.separator + "data" + File.separator
					+ "projects" + File.separator + projectName
					+ File.separator + "configs";
			File objFile = new File(file + File.separator
					+ "TestConfiguration.xml");
			if (objFile.exists()) {
				builder = documentBuilderFactory.newDocumentBuilder();
				document = builder.parse(objFile);
				document.getDocumentElement().normalize();
				NodeList nodeList = document.getElementsByTagName("TestConfig");
				Configuration configuration = null;
				for (int i = 0; i < nodeList.getLength(); i++) {

					configuration = new Configuration();

					NodeList childNode = nodeList.item(i).getChildNodes();

					for (int a = 0; a < childNode.getLength(); a++) {
						if ("hostname".equalsIgnoreCase(childNode.item(a)
								.getNodeName())) {
							configuration.setHost(childNode.item(a)
									.getTextContent());

						} else if ("browsertype".equalsIgnoreCase(childNode
								.item(a).getNodeName())) {
							configuration.setBrowserType(childNode.item(a)
									.getTextContent());

						} else if ("browserversion".equalsIgnoreCase(childNode
								.item(a).getNodeName())) {
							configuration.setBrowserVersion(childNode.item(a)
									.getTextContent());
						} else if ("portnumber".equalsIgnoreCase(childNode
								.item(a).getNodeName())) {
							configuration.setPort(childNode.item(a)
									.getTextContent());
						} else if ("platform".equalsIgnoreCase(childNode
								.item(a).getNodeName())) {
							configuration.setOsVersion(childNode.item(a)
									.getTextContent());
						}

					}

					suiteSet.put(
							nodeList.item(i).getAttributes()
									.getNamedItem("name").getNodeValue(),
							configuration);
				}
			}
		} catch (Exception e) {
			logger.error(e.getMessage());
		}

		return suiteSet;
	}

	public List<Configuration> getConfigurationsList(String path,
			String projectName) {
		List<Configuration> suiteSet = new ArrayList<Configuration>();

		try {
			String file = path + File.separator + "data" + File.separator
					+ "projects" + File.separator + projectName
					+ File.separator + "configs";
			File objFile = new File(file + File.separator
					+ "TestConfiguration.xml");
			if (objFile.exists()) {
				builder = documentBuilderFactory.newDocumentBuilder();
				document = builder.parse(objFile);
				document.getDocumentElement().normalize();
				NodeList nodeList = document.getElementsByTagName("TestConfig");
				Configuration configuration = null;
				for (int i = 0; i < nodeList.getLength(); i++) {

					configuration = new Configuration();

					NodeList childNode = nodeList.item(i).getChildNodes();

					for (int a = 0; a < childNode.getLength(); a++) {
						configuration.setConfigName(nodeList.item(i)
								.getAttributes().getNamedItem("name")
								.getNodeValue());
						if ("hostname".equalsIgnoreCase(childNode.item(a)
								.getNodeName())) {
							configuration.setHost(childNode.item(a)
									.getTextContent());

						}
						if ("browsertype".equalsIgnoreCase(childNode.item(a)
								.getNodeName())) {
							configuration.setBrowserType(childNode.item(a)
									.getTextContent());

						}
						if ("browserversion".equalsIgnoreCase(childNode.item(a)
								.getNodeName())) {
							configuration.setBrowserVersion(childNode.item(a)
									.getTextContent());
						}
						if ("portnumber".equalsIgnoreCase(childNode.item(a)
								.getNodeName())) {
							configuration.setPort(childNode.item(a)
									.getTextContent());
						}

					}

					suiteSet.add(configuration);
				}
			}
		} catch (Exception e) {
			logger.error(e.getMessage());
		}

		return suiteSet;
	}

	public int removeConfiguration(String path, String projectName,
			String configName) {
		String file = path + File.separator + "data" + File.separator
				+ "projects" + File.separator + projectName + File.separator
				+ "configs" + File.separator + "TestConfiguration.xml";
		return removeNode(file, "TestConfig", "name", configName);
	}

	public boolean isConfigurationExists(String path, String projectName,
			String configName) {
		String file = path + File.separator + "data" + File.separator
				+ "projects" + File.separator + projectName + File.separator
				+ "configs" + File.separator + "TestConfiguration.xml";
		return isNodeExists(file, "TestConfig", "name", configName);

	}

	public boolean checkTestStepName(String path, String projectName,
			String testStepName) {
		String file = path + File.separator + "data" + File.separator
				+ "projects" + File.separator + projectName + File.separator
				+ "test-input" + File.separator + "TestSteps.xml";

		return isNodeExists(file, "StepName", "name", testStepName);
	}

	public int updateTestSteps(String path, String projectName,
			List<TestSteps> steps, String stepName, String repeatCount) {
		try {
			String file = path + File.separator + "data" + File.separator
					+ "projects" + File.separator + projectName
					+ File.separator + "test-input" + File.separator
					+ "TestSteps.xml";
			File testStepsFile = new File(file);

			builder = documentBuilderFactory.newDocumentBuilder();

			if (testStepsFile.exists()) {
				document = builder.parse(testStepsFile);
				document.getDocumentElement().normalize();
			} else {
				document = builder.newDocument();
			}
			Element rootElement = document.getDocumentElement();

			Element stepNameElement = document.createElement("StepName");
			stepNameElement.setAttribute("count", repeatCount);
			stepNameElement.setAttribute("name", stepName);

			for (TestSteps testSteps : steps) {
				Element stepElement = document.createElement("step");
				stepElement.setAttribute("aComponent",
						testSteps.getComponentName());
				stepElement.setAttribute("bAction", testSteps.getActionName());
				stepElement.setAttribute("cExpectedValue",
						testSteps.getMessageName());
				stepNameElement.appendChild(stepElement);
			}

			rootElement.appendChild(stepNameElement);

			transformer = transformerFactory.newTransformer();
			transformer.setOutputProperty(OutputKeys.INDENT, "yes");
			DOMSource source = new DOMSource(document);
			StreamResult result = new StreamResult(testStepsFile);
			transformer.transform(source, result);

			return 0;
		} catch (Exception e) {
			logger.error(e.getMessage());
			return 1;
		}

	}

	public int updateMessageReferenceFile(String path, String projectName,
			Map<String, String> msgMap) {

		try {

			String file = path + File.separator + "data" + File.separator
					+ "projects" + File.separator + projectName
					+ File.separator + "test-input";
			File objFile = new File(file + File.separator + "Messages.xml");

			builder = documentBuilderFactory.newDocumentBuilder();

			if (objFile.exists()) {
				document = builder.parse(objFile);
				document.getDocumentElement().normalize();
			} else {
				document = builder.newDocument();
			}

			Element rootElement = document.getDocumentElement();

			Set<Map.Entry<String, String>> msgSet = msgMap.entrySet();

			for (Entry<String, String> entry : msgSet) {
				Element element = document.createElement("Msg");
				element.setAttribute("name", entry.getKey());
				element.setTextContent(entry.getValue());

				rootElement.appendChild(element);
			}

			transformer = transformerFactory.newTransformer();
			transformer.setOutputProperty(OutputKeys.INDENT, "yes");
			DOMSource source = new DOMSource(document);
			StreamResult result = new StreamResult(objFile);
			transformer.transform(source, result);

			return 0;
		} catch (Exception e) {
			logger.error(e.getMessage());
			return -1;
		}

	}

	public void createMessagesFile(String path) throws Exception {

		File msgFile = new File(path + File.separator + "Messages.xml");
		if (!msgFile.exists()) {
			builder = documentBuilderFactory.newDocumentBuilder();
			document = builder.newDocument();
			Element rootElement = document.createElement("Messages");
			document.appendChild(rootElement);
			transformer = transformerFactory.newTransformer();
			DOMSource source = new DOMSource(document);
			StreamResult result = new StreamResult(msgFile);
			transformer.transform(source, result);
		}

	}

	public boolean deleteMessagesFile(String path) throws Exception {

		File msgFile = new File(path + File.separator + "Messages.xml");
		if (msgFile.exists())
			return msgFile.delete();
		return false;

	}

	public void createTestDetails(String path) throws Exception {

		File testDetailsFile = new File(path + File.separator
				+ "TestDetails.xml");
		if (!testDetailsFile.exists()) {
			builder = documentBuilderFactory.newDocumentBuilder();
			document = builder.newDocument();
			Element rootElement = document.createElement("TestDetails");
			document.appendChild(rootElement);
			transformer = transformerFactory.newTransformer();
			DOMSource source = new DOMSource(document);
			StreamResult result = new StreamResult(testDetailsFile);
			transformer.transform(source, result);
		}

	}

	public void createTestSteps(String path) throws Exception {

		File testDetailsFile = new File(path + File.separator + "TestSteps.xml");
		if (!testDetailsFile.exists()) {
			builder = documentBuilderFactory.newDocumentBuilder();
			document = builder.newDocument();
			Element rootElement = document.createElement("TestSteps");
			document.appendChild(rootElement);
			transformer = transformerFactory.newTransformer();
			DOMSource source = new DOMSource(document);
			StreamResult result = new StreamResult(testDetailsFile);
			transformer.transform(source, result);
		}

	}

	public void createTestsToLoad(String path, String moduleName)
			throws Exception {

		File testDetailsFile = new File(path + File.separator + moduleName
				+ File.separator + "TestsToLoad.xml");
		if (!testDetailsFile.exists()) {
			builder = documentBuilderFactory.newDocumentBuilder();
			document = builder.newDocument();
			Element rootElement = document.createElement("TestCases");
			document.appendChild(rootElement);
			transformer = transformerFactory.newTransformer();
			DOMSource source = new DOMSource(document);
			StreamResult result = new StreamResult(testDetailsFile);
			transformer.transform(source, result);
		}

	}

	public boolean isProjectExists(String path, String projectName) {
		String file = path + File.separator + "data" + File.separator
				+ "projects" + File.separator + "projects.xml";

		return isNodeExists(file, "proj", "", projectName);
	}

	public int createProjects(String path, String projectName) {
		try {
			File projectFile = new File(path + File.separator + "data"
					+ File.separator + "projects" + File.separator
					+ "projects.xml");

			builder = documentBuilderFactory.newDocumentBuilder();
			document = builder.newDocument();

			if (!projectFile.exists()) {
				Element rootElement = document.createElement("projects");
				document.appendChild(rootElement);
				Element cat = document.createElement("proj");
				cat.setTextContent(projectName);
				rootElement.appendChild(cat);
			} else {

				if (getProjects(path).contains(projectName)) {
					createProjectFolder(path, projectName);
					return 1;// duplicate
				}

				document = builder.parse(projectFile);
				document.getDocumentElement().normalize();

				Element cat = document.createElement("proj");
				cat.setTextContent(projectName);
				document.getDocumentElement().appendChild(cat);

			}

			transformer = transformerFactory.newTransformer();
			DOMSource source = new DOMSource(document);
			StreamResult result = new StreamResult(projectFile);
			transformer.transform(source, result);
			createProjectFolder(path, projectName);
			return 0;
		} catch (Exception e) {
			logger.error(e.getMessage());
			return -1;

		}
	}

	public int removeProject(String path, String project) {
		// remove project means here remove project name from xml file
		// File fpath = new File(path + File.separator + "data" + File.separator
		// + "projects" + File.separator +project);
		String file = path + File.separator + "data" + File.separator
				+ "projects" + File.separator + "projects.xml";
		// deleteDirectory(fpath);
		return removeNode(file, "proj", "", project);
	}

	public boolean checkTestCaseName(String path, String projectName,
			String testCaseName) {
		String file = path + File.separator + "data" + File.separator
				+ "projects" + File.separator + projectName + File.separator
				+ "test-input" + File.separator + "TestDetails.xml";

		return isNodeExists(file, "Test", "name", testCaseName);
	}

	public int updateTestCasesFiles(String path, String projectName,
			String moduleName, List<String> testSteps, String testcase_name) {

		try {

			String file = path + File.separator + "data" + File.separator
					+ "projects" + File.separator + projectName
					+ File.separator + "test-input";
			File detailsFile = new File(file + File.separator
					+ "TestDetails.xml");

			File testsToLoad = new File(file + File.separator + moduleName
					+ File.separator + "TestsToLoad.xml");

			// and updata TestsToLoad.xml file with Test Case name
			updateTestsToLoad(testcase_name, testsToLoad);

			// add testSteps to TestDetails.xml
			updateTestDetails(testcase_name, testSteps, detailsFile);
			return 0;
		} catch (Exception e) {
			logger.error(e.getMessage());
			return -1;
		}
	}

	public List<String> getCategories(String path, String project) {
		List<String> categories = new ArrayList<String>();
		try {

			DocumentBuilder builder = documentBuilderFactory
					.newDocumentBuilder();
			Document document = builder.newDocument();

			File file = new File(path + File.separator + "data"
					+ File.separator + "projects" + File.separator + project
					+ File.separator + "test-input" + File.separator
					+ "Categories.xml");
			if (file.exists()) {
				document = builder.parse(file);
				document.getDocumentElement().normalize();
				NodeList nodeList = document.getElementsByTagName("cat");
				for (int i = 0; i < nodeList.getLength(); i++) {
					categories
							.add((((Node) nodeList.item(i)).getTextContent()));
				}
			}
		} catch (Exception e) {
			logger.error(e.getMessage());
		}

		return categories;
	}

	public Map<String, List<String>> getCategoriesTestList(String path,
			String project) {
		Map<String, List<String>> categories = new LinkedHashMap<String, List<String>>();
		try {

			DocumentBuilder builder = documentBuilderFactory
					.newDocumentBuilder();
			Document document = builder.newDocument();

			File file = new File(path + File.separator + "data"
					+ File.separator + "projects" + File.separator + project
					+ File.separator + "test-input" + File.separator
					+ "Categories.xml");
			if (file.exists()) {
				document = builder.parse(file);
				document.getDocumentElement().normalize();
				NodeList nodeList = document.getElementsByTagName("cat");
				for (int i = 0; i < nodeList.getLength(); i++) {
					categories.put(
							(((Node) nodeList.item(i)).getTextContent()),
							new ArrayList<String>());
				}
				Set<Entry<String, List<String>>> s = categories.entrySet();

				for (Entry<String, List<String>> entry : s) {

					addTestToLoad(path, project, entry.getKey(),
							entry.getValue());
				}
			}
		} catch (Exception e) {
			logger.error(e.getMessage());
		}

		return categories;
	}

	public void addTestToLoad(String path, String project, String category,
			List<String> testSet) {

		try {
			File testFile = new File(path + File.separator + "data"
					+ File.separator + "projects" + File.separator + project
					+ File.separator + "test-input" + File.separator + category
					+ File.separator + "TestsToLoad.xml");

			builder = documentBuilderFactory.newDocumentBuilder();
			document = builder.parse(testFile);
			document.getDocumentElement().normalize();
			NodeList nodeList = document.getElementsByTagName("Test");
			for (int i = 0; i < nodeList.getLength(); i++) {
				testSet.add((((Node) nodeList.item(i)).getTextContent()));
			}
		} catch (Exception e) {
			logger.error(e.getMessage());
		}

	}

	public boolean checkSuiteName(String path, String projectName,
			String suiteName) {
		String file = path + File.separator + "data" + File.separator
				+ "projects" + File.separator + projectName + File.separator
				+ "configs" + File.separator + "Suite.xml";

		return isNodeExists(file, "Suite", "name", suiteName);
	}

	public int updateTestSuiteInfo(String path, String projectName,
			String suiteName, List<TestSuite> testSuites) {

		try {

			String file = path + File.separator + "data" + File.separator
					+ "projects" + File.separator + projectName
					+ File.separator + "configs";

			File suiteFolder = new File(file + File.separator + suiteName);
			if (!suiteFolder.exists()) {
				suiteFolder.mkdirs();
			}

			String urls = "";
			String cat = "";
			for (TestSuite testSuite : testSuites) {

				urls += "," + testSuite.getUrl();
				cat += "," + testSuite.getModule();
			}

			File suiteFile = new File(file + File.separator + "Suite.xml");

			builder = documentBuilderFactory.newDocumentBuilder();
			Element rootElement = null;
			if (suiteFile.exists()) {
				document = builder.parse(suiteFile);
				document.getDocumentElement().normalize();
				rootElement = document.getDocumentElement();
			} else {
				document = builder.newDocument();
				rootElement = document.createElement("Suites");
				document.appendChild(rootElement);
			}

			Element suite = document.createElement("Suite");
			suite.setAttribute("name", suiteName);
			rootElement.appendChild(suite);

			Element url = document.createElement("urls");
			url.setTextContent(urls.substring(1));
			suite.appendChild(url);

			Element cats = document.createElement("cats");
			cats.setTextContent(cat.substring(1));
			suite.appendChild(cats);

			rootElement.appendChild(suite);

			transformer = transformerFactory.newTransformer();
			transformer.setOutputProperty(OutputKeys.INDENT, "yes");
			DOMSource source = new DOMSource(document);
			StreamResult result = new StreamResult(suiteFile);
			transformer.transform(source, result);
			return createSuiteFolder(path, projectName, suiteName, testSuites);

		} catch (Exception e) {

			logger.error(e.getMessage());
			return -1;
		}

	}

	public int createSuiteFolder(String path, String projectName,
			String suiteName, List<TestSuite> testSuites) {

		try {

			File suiteDir = new File(path + File.separator + "data"
					+ File.separator + "projects" + File.separator
					+ projectName + File.separator + "configs" + File.separator
					+ suiteName);

			if (!suiteDir.exists()) {
				suiteDir.mkdir();
			}

			for (TestSuite testSuite : testSuites) {

				File testSuiteXML = new File(path + File.separator + "data"
						+ File.separator + "projects" + File.separator
						+ projectName + File.separator + "configs"
						+ File.separator + suiteName + File.separator
						+ suiteName + "_" + testSuite.getModule() + ".xml");

				DocumentBuilder builder = documentBuilderFactory
						.newDocumentBuilder();
				Document document = builder.newDocument();

				Element rootElement = document.createElement("TestCases");
				document.appendChild(rootElement);

				String[] testCases = testSuite.getTestCases();

				for (String testCase : testCases) {
					Element testCaseElement = document.createElement("Test");

					testCaseElement.appendChild(document
							.createTextNode(testCase));
					rootElement.appendChild(testCaseElement);
				}

				transformer = transformerFactory.newTransformer();
				transformer.setOutputProperty(OutputKeys.INDENT, "yes");
				DOMSource source = new DOMSource(document);
				StreamResult result = new StreamResult(testSuiteXML);
				transformer.transform(source, result);
			}
			return 0;
		} catch (Exception e) {
			logger.error(e.getMessage());
			return -1;
		}

	}

	public List<String> getTestSteps(String path, String projectName) {
		List<String> objMap = new ArrayList<String>();

		try {
			String file = path + File.separator + "data" + File.separator
					+ "projects" + File.separator + projectName
					+ File.separator + "test-input" + File.separator
					+ "TestSteps.xml";
			File objFile = new File(file);

			String objName = "";

			builder = documentBuilderFactory.newDocumentBuilder();
			document = builder.parse(objFile);
			document.getDocumentElement().normalize();
			NodeList nodeList = document.getElementsByTagName("StepName");
			for (int i = 0; i < nodeList.getLength(); i++) {

				objName = nodeList.item(i).getAttributes().getNamedItem("name")
						.getNodeValue();

				objMap.add(objName);
			}

		} catch (Exception e) {
			logger.error(e.getMessage());
		}

		return objMap;
	}

	public List<String> getTestCases(String path, String project, String module) {
		List<String> testCases = new ArrayList<String>();

		try {
			String file = path + File.separator + "data" + File.separator
					+ "projects" + File.separator + project + File.separator
					+ "test-input" + File.separator + module + File.separator
					+ "TestsToLoad.xml";
			File objFile = new File(file);

			builder = documentBuilderFactory.newDocumentBuilder();
			document = builder.parse(objFile);
			document.getDocumentElement().normalize();
			NodeList nodeList = document.getElementsByTagName("Test");
			for (int i = 0; i < nodeList.getLength(); i++) {
				testCases.add(nodeList.item(i).getTextContent());
			}

		} catch (Exception e) {
			logger.error(e.getMessage());
		}
		return testCases;
	}

	public List<String> getSelCaseSteps(String path, String project,
			String module, String testCase) {
		List<String> testCases = new ArrayList<String>();

		try {
			String file = path + File.separator + "data" + File.separator
					+ "projects" + File.separator + project + File.separator
					+ "test-input" + File.separator + "TestDetails.xml";
			File objFile = new File(file);

			builder = documentBuilderFactory.newDocumentBuilder();
			document = builder.parse(objFile);
			document.getDocumentElement().normalize();
			NodeList nodeList = document.getElementsByTagName("Test");
			for (int i = 0; i < nodeList.getLength(); i++) {
				String caseName = nodeList.item(i).getAttributes()
						.getNamedItem("name").getNodeValue();
				if (caseName.equals(testCase)) {

					Node node = nodeList.item(i);
					if (node.getNodeType() == Node.ELEMENT_NODE) {
						Element element = (Element) node;
						NodeList snodelist = element
								.getElementsByTagName("Step");
						for (int j = 0; j < snodelist.getLength(); j++) {
							testCases.add(snodelist.item(j).getTextContent());
						}
					}
					break;
				}
			}

		} catch (Exception e) {
			logger.error(e.getMessage());
		}
		return testCases;
	}

	public Map<String, Object> getTestSteps(String path, String projectName,
			String moduleName, String stepName) {

		Map<String, Object> objMap = new LinkedHashMap<String, Object>();

		try {
			String file = path + File.separator + "data" + File.separator
					+ "projects" + File.separator + projectName
					+ File.separator + "test-input" + File.separator
					+ "TestSteps.xml";
			File objFile = new File(file);
			String objName = "";

			String stepcount = "";

			List<TestSteps> stepsData = new ArrayList<TestSteps>();

			builder = documentBuilderFactory.newDocumentBuilder();
			document = builder.parse(objFile);
			document.getDocumentElement().normalize();
			NodeList nodeList = document.getElementsByTagName("StepName");

			for (int i = 0; i < nodeList.getLength(); i++) {
				objName = nodeList.item(i).getAttributes().getNamedItem("name")
						.getNodeValue();

				if (objName.equals(stepName)) {
					stepcount = nodeList.item(i).getAttributes()
							.getNamedItem("count").getNodeValue();
					Node node = nodeList.item(i);
					if (node.getNodeType() == Node.ELEMENT_NODE) {
						Element element = (Element) node;
						NodeList snodelist = element
								.getElementsByTagName("step");
						for (int j = 0; j < snodelist.getLength(); j++) {
							TestSteps data = new TestSteps();
							data.setComponentName(snodelist.item(j)
									.getAttributes().getNamedItem("aComponent")
									.getNodeValue());
							data.setActionName(snodelist.item(j)
									.getAttributes().getNamedItem("bAction")
									.getNodeValue());
							data.setMessageName(snodelist.item(j)
									.getAttributes()
									.getNamedItem("cExpectedValue")
									.getNodeValue());
							stepsData.add(data);
						}
					}
					objMap.put("count", stepcount);
					break;
				}

			}

			objMap.put("name", stepName);
			objMap.put("data", stepsData);

		} catch (Exception e) {
			logger.error(e.getMessage());
		}
		return objMap;
	}

	public List<String> getProjects(String path) {
		List<String> projects = new ArrayList<String>();
		try {

			DocumentBuilder builder = documentBuilderFactory
					.newDocumentBuilder();
			Document document = builder.newDocument();

			File projectFile = new File(path + File.separator + "data"
					+ File.separator + "projects" + File.separator
					+ "projects.xml");

			if (!projectFile.exists()) {
				return projects;
			}

			document = builder.parse(projectFile);
			document.getDocumentElement().normalize();
			NodeList nodeList = document.getElementsByTagName("proj");
			for (int i = 0; i < nodeList.getLength(); i++) {
				projects.add((((Node) nodeList.item(i)).getTextContent()));
			}

		} catch (Exception e) {
			logger.error(e.getMessage());
		}

		return projects;
	}

	public List<String> getActionList(String path) {
		List<String> actionItems = new ArrayList<String>();

		try {

			builder = documentBuilderFactory.newDocumentBuilder();

			File actionsFile = new File(path + File.separator + "data"
					+ File.separator + "projects" + File.separator
					+ "ActionList.xml");

			document = builder.parse(actionsFile);
			document.getDocumentElement().normalize();
			NodeList nodeList = document.getElementsByTagName("Action");
			for (int i = 0; i < nodeList.getLength(); i++) {
				actionItems.add(((Node) nodeList.item(i)).getTextContent());
			}

		} catch (Exception e) {
			logger.error(e.getMessage());
		}

		return actionItems;
	}

	public List<String> getComponentList(String path) {
		List<String> compList = new ArrayList<String>();

		try {

			builder = documentBuilderFactory.newDocumentBuilder();

			File actionsFile = new File(path + File.separator + "data"
					+ File.separator + "projects" + File.separator
					+ "CompTypeList.xml");

			document = builder.parse(actionsFile);
			document.getDocumentElement().normalize();
			NodeList nodeList = document.getElementsByTagName("CompType");
			for (int i = 0; i < nodeList.getLength(); i++) {
				compList.add(((Node) nodeList.item(i)).getTextContent());
			}

		} catch (Exception e) {
			logger.error(e.getMessage());
		}

		return compList;
	}

	public void removeMessageNodes(String path, String projectName,
			Map<String, String> msgMap) {
		String file = path + File.separator + "data" + File.separator
				+ "projects" + File.separator + projectName + File.separator
				+ "test-input" + File.separator + "Messages.xml";

		Set<Map.Entry<String, String>> msgSet = msgMap.entrySet();
		List<String> lst = new ArrayList<String>();
		for (Entry<String, String> entry : msgSet) {
			lst.add(entry.getKey());

		}
		removeNodes(file, "Msg", "name", lst);
	}

	public void removeObjectIdNodes(String path, String projectName,
			Map<String, String> objmap) {
		String file = path + File.separator + "data" + File.separator
				+ "projects" + File.separator + projectName + File.separator
				+ "test-input" + File.separator + "ObjectReference.xml";

		Set<Map.Entry<String, String>> objSet = objmap.entrySet();
		// remove nodes
		List<String> lst = new ArrayList<String>();
		for (Entry<String, String> entry : objSet) {
			lst.add(entry.getKey());
		}
		removeNodes(file, "RefValues", "aName", lst);
		// end of remove nodes
	}

	public void removeTestStepNode(String path, String project,
			String moduleName, String value) {
		String filepath = path + File.separator + "data" + File.separator
				+ "projects" + File.separator + project + File.separator
				+ "test-input" + File.separator + "TestSteps.xml";
		removeNode(filepath, "StepName", "name", value);

	}

	private int removeNode(String filepath, String tag, String attribute,
			String value) {
		try {

			builder = documentBuilderFactory.newDocumentBuilder();
			document = builder.parse(filepath);

			List<Node> lstnodes = new ArrayList<Node>();
			NodeList nodeList = document.getElementsByTagName(tag);
			if (attribute.equals("")) {
				String tagContent;
				for (int i = 0; i < nodeList.getLength(); i++) {
					tagContent = nodeList.item(i).getTextContent();
					if (tagContent.equals(value)) {
						lstnodes.add((Node) nodeList.item(i));
						// break;
					}
				}

			} else {
				String objName;
				for (int i = 0; i < nodeList.getLength(); i++) {
					objName = nodeList.item(i).getAttributes()
							.getNamedItem(attribute).getNodeValue();
					Node node = nodeList.item(i);

					if (objName.equals(value)) {
						lstnodes.add(node);
						// break;
					}
				}
			}
			for (Node node : lstnodes) {
				Node parent = node.getParentNode();
				parent.removeChild(node);
			}
			// write the content into xml file
			//
			Transformer transformer = transformerFactory.newTransformer();
			DOMSource source = new DOMSource(document);
			StreamResult result = new StreamResult(new File(filepath));
			transformer.transform(source, result);

			return 0;
		} catch (Exception e) {
			logger.error(e.getMessage());
			return -1;
		}
	}

	private void removeNodes(String filepath, String tag, String attribute,
			List<String> values) {
		try {

			builder = documentBuilderFactory.newDocumentBuilder();
			document = builder.parse(filepath);

			String objName;
			List<Node> lstnodes = new ArrayList<Node>();
			NodeList nodeList = document.getElementsByTagName(tag);
			for (String value : values) {
				for (int i = 0; i < nodeList.getLength(); i++) {
					objName = nodeList.item(i).getAttributes()
							.getNamedItem(attribute).getNodeValue();
					Node node = nodeList.item(i);

					if (objName.equals(value)) {
						lstnodes.add(node);
						// break;
					}
				}
			}

			for (Node node : lstnodes) {
				Node parent = node.getParentNode();
				parent.removeChild(node);
			}
			// write the content into xml file
			//
			Transformer transformer = transformerFactory.newTransformer();
			DOMSource source = new DOMSource(document);
			StreamResult result = new StreamResult(new File(filepath));
			transformer.transform(source, result);

		} catch (Exception e) {
			logger.error(e.getMessage());
		}
	}

	private boolean isNodeExists(String filepath, String contenttag,
			String attribute, String value) {
		try {

			builder = documentBuilderFactory.newDocumentBuilder();
			document = builder.parse(filepath);
			String objValue;

			NodeList nodeList = document.getElementsByTagName(contenttag);
			if (attribute.equals("")) {
				for (int i = 0; i < nodeList.getLength(); i++) {
					objValue = nodeList.item(i).getTextContent();
					if (objValue.equals(value)) {
						return true;

					}
				}
			} else {
				for (int i = 0; i < nodeList.getLength(); i++) {
					objValue = nodeList.item(i).getAttributes()
							.getNamedItem(attribute).getNodeValue();
					if (objValue.equals(value)) {
						return true;

					}
				}
			}

		} catch (Exception e) {
			logger.error(e.getMessage());

		}
		return false;
	}

	public void prepareTestNGSuite(String username, String realPath,
			String projectName, TestSuite testSuite,
			Configuration configuration, String suiteName, String configName,
			List<XmlSuite> suites, List<String> reportList, String time) {

		try {
			String[] moduleNames = testSuite.getModule().split(",");
			String[] urlNames = testSuite.getUrl().split(",");

			for (int i = 0; i < moduleNames.length; i++) {

				XmlSuite suite = new XmlSuite();
				suite.setName(suiteName + "_" + configName + "_"
						+ moduleNames[i]);
				Map<String, String> parameters = new LinkedHashMap<String, String>();
				parameters.put("username", username);
				parameters.put("path", realPath);
				parameters.put("project", projectName);
				parameters.put("portnumber", configuration.getPort());
				parameters.put("hostname", configuration.getHost());
				parameters.put("module", moduleNames[i]);
				parameters.put("time", time);
				parameters.put("browser", configuration.getBrowserType());
				parameters.put("version", configuration.getBrowserVersion());
				parameters.put("platform", configuration.getOsVersion());
				parameters.put("URL", urlNames[i]);
				parameters.put("suitename", suiteName);

				suite.setParameters(parameters);
				reportList.add(suiteName + "_" + configName + "_"
						+ moduleNames[i]);
				XmlTest test = new XmlTest(suite);
				test.setName(suiteName + "_" + configName + "_"
						+ moduleNames[i]);
				List<XmlClass> classes = new ArrayList<XmlClass>();
				classes.add(new XmlClass("automation.scripts.TestDriver"));
				classes.add(new XmlClass("automation.scripts.Components"));

				test.setXmlClasses(classes);
				suites.add(suite);

				logger.info("	SUITE_NAME  ::: [" + suiteName
						+ "] 	CONFIG_NAME  :::	[" + configName
						+ "]   MODULE  :::  [" + moduleNames[i]
						+ "]   APP_URL  :::  [" + urlNames[i] + " ]  ");

			}
		} catch (Exception e) {
			logger.error(e.getMessage());
		}

	}

	public int updateTestsToLoad(String Testcase, File file) throws Exception {

		try {
			builder = documentBuilderFactory.newDocumentBuilder();
			if (file.exists()) {
				document = builder.parse(file);
				document.getDocumentElement().normalize();
			} else {
				document = builder.newDocument();
			}

			Element rootElement = document.getDocumentElement();

			NodeList tagsNodeList = document.getElementsByTagName("Test");

			for (int i = 0; i < tagsNodeList.getLength(); i++) {
				Node childNode = tagsNodeList.item(i);
				String str = childNode.getTextContent();
				if (str.compareToIgnoreCase(Testcase) == 0) {
					// remove node if exists
					Node parent = childNode.getParentNode();
					parent.removeChild(childNode);

				}
			}

			Element element = document.createElement("Test");
			element.setTextContent(Testcase);
			rootElement.appendChild(element);

			transformer = transformerFactory.newTransformer();
			transformer.setOutputProperty(OutputKeys.INDENT, "yes");
			DOMSource source = new DOMSource(document);
			StreamResult result = new StreamResult(file);
			transformer.transform(source, result);

			return 0;
		} catch (Exception e) {
			logger.error(e.getMessage());
			return -1;
		}
	}

	public int updateTestDetails(String Name, List<String> Steps, File file)
			throws Exception {
		try {
			builder = documentBuilderFactory.newDocumentBuilder();
			if (file.exists()) {
				document = builder.parse(file);
				document.getDocumentElement().normalize();
			} else {
				document = builder.newDocument();
			}

			Element rootElement = document.getDocumentElement();

			NodeList tagsNodeList = document.getElementsByTagName("Test");
			for (int i = 0; i < tagsNodeList.getLength(); i++) {
				Node childNode = tagsNodeList.item(i);
				String str = tagsNodeList.item(i).getAttributes()
						.getNamedItem("name").getNodeValue();
				if (str.equalsIgnoreCase(Name)) {
					Node parent = childNode.getParentNode();
					parent.removeChild(childNode);
				}
			}

			Element Test = document.createElement("Test");
			Test.setAttribute("name", Name);

			for (String step : Steps) {
				Element Step1 = document.createElement("Step");
				Step1.setTextContent(step);
				Test.appendChild(Step1);
			}

			rootElement.appendChild(Test);

			transformer = transformerFactory.newTransformer();
			transformer.setOutputProperty(OutputKeys.INDENT, "yes");
			DOMSource source = new DOMSource(document);
			StreamResult result = new StreamResult(file);
			transformer.transform(source, result);

			return 0;
		} catch (Exception e) {
			logger.error(e.getMessage());
			return -1;
		}
	}

	public List<String> getProjectFiles(String path, String project) {

		List<String> prjfiles = new ArrayList<String>();
		try {

			File moduleDir = new File(path + File.separator + "data"
					+ File.separator + "projects" + File.separator + project
					+ File.separator + "test-input");
			for (File file : moduleDir.listFiles(new XMLFilter())) {
				prjfiles.add(file.getName());
			}

		} catch (Exception e) {
			logger.error(e.getMessage());
		}
		return prjfiles;
	}

	public List<String> getModuleFiles(String path, String project,
			String moduleName) {
		List<String> datafiles = new ArrayList<String>();

		try {

			File moduleDir = new File(path + File.separator + "data"
					+ File.separator + "projects" + File.separator + project
					+ File.separator + "test-input" + File.separator
					+ moduleName);
			for (File file : moduleDir.listFiles(new XMLFilter())) {
				datafiles.add(file.getName());
			}

		} catch (Exception e) {
			logger.error(e.getMessage());
		}
		return datafiles;
	}

	public String getFileData(String path, String project, String moduleName,
			String fname, String dir) {

		String filedata = "";
		String mpath = "";
		if (dir.equals("mod")) {
			mpath = File.separator + moduleName;
		}

		File reqFile = new File(path + File.separator + "data" + File.separator
				+ "projects" + File.separator + project + File.separator
				+ "test-input" + mpath + File.separator + fname);

		try {
			ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
			InputStream input = new FileInputStream(reqFile);

			byte[] dataBuffer = new byte[4096];
			int numberBytes = 0;
			while ((numberBytes = input.read(dataBuffer, 0, 4096)) != -1) {
				byteStream.write(dataBuffer, 0, numberBytes);
			}
			filedata = new String(byteStream.toByteArray());
			input.close();
		} catch (IOException e) {
			logger.error(e.getMessage());
			return null;
		}
		return filedata;
	}

	public int setFileData(String path, String project, String moduleName,
			String fname, String data, String dir) {
		String mpath = "";
		if (dir.equals("mod")) {
			mpath = File.separator + moduleName;
		}

		File reqFile = new File(path + File.separator + "data" + File.separator
				+ "projects" + File.separator + project + File.separator
				+ "test-input" + mpath + File.separator + fname);
		try {
			BufferedWriter bw = new BufferedWriter(new FileWriter(reqFile,
					false));
			bw.write(data);
			bw.newLine();
			bw.close();
			return 0;
		} catch (Exception e) {
			logger.error(e.getMessage());
			return -1;
		}
	}

	static public void deleteDirectory(File path) {
		if (path == null)
			return;
		if (path.exists()) {
			for (File f : path.listFiles()) {
				if (f.isDirectory()) {
					deleteDirectory(f);
					f.delete();
				} else {
					f.delete();
				}
			}
			path.delete();
		}
	}

	public List<String> getReportUsers(String path, String projectName) {
		File reportFolder = new File(path + File.separator + "data"
				+ File.separator + "projects" + File.separator + projectName
				+ File.separator + "test-output");
		List<String> userList = new ArrayList<String>();
		if (reportFolder.exists()) {
			for (File file : reportFolder.listFiles(new UserFilter())) {
				if (file.isDirectory()) {
					userList.add(file.getName());
				}
			}
		}
		return userList;

	}

	List<Reports> reports;
	Reports report;

	public Map<String, List<Reports>> getUserReports(String contPath,
			String path, String projectName, String userName) {
		Map<String, List<Reports>> reportsMap = new LinkedHashMap<String, List<Reports>>();

		String mainFolder = path + File.separator + "data" + File.separator
				+ "projects" + File.separator + projectName + File.separator
				+ "test-output" + File.separator + userName;

		File folder = new File(mainFolder);

		if (folder.isDirectory()) {

			List<String> listVTR = Arrays.asList(folder.list());
			Collections.sort(listVTR);
			Collections.reverse(listVTR);

			for (String reportFolderTime : listVTR) {

				try {

					String contextPath = contPath + "/" + "data" + "/"
							+ "projects" + "/" + projectName + "/"
							+ "test-output" + "/" + userName + "/"
							+ reportFolderTime;

					String imagesFolder = contPath
							+ "/tester/screenshots.htm?time="
							+ reportFolderTime;

					reports = new ArrayList<Reports>();

					String childFolder = mainFolder + File.separator
							+ reportFolderTime;

					File childFileFolder = new File(childFolder);

					for (String suiteName : childFileFolder
							.list(new HTMLFilter())) {

						report = new Reports();
						report.setReportNG(contextPath + "/html/overview.html");
						report.setSuiteName(suiteName.replaceAll(".html", ""));
						report.setiReport(contextPath + "/" + suiteName);
						report.setLogger(imagesFolder);
						report.settNGReport(contextPath + "/"
								+ suiteName.replaceAll(".html", "") + "/"
								+ suiteName);
						report.setMailReport(contextPath
								+ "/emailable-report.html");
						reports.add(report);

					}

					reportsMap.put(AdtfDateUtil.formatAsDate(reportFolderTime),
							reports);

				} catch (Exception e) {
					logger.error("report folder error " + e.getMessage());
				}

			}

		}
		return reportsMap;
	}

	public boolean clearAllReports(String path, String projectName) {
		String mainFolder = path + File.separator + "data" + File.separator
				+ "projects" + File.separator + projectName + File.separator
				+ "test-output";
		List<String> users = getReportUsers(path, projectName);
		try {
			for (String username : users) {
				File userReportsFolder = new File(mainFolder + File.separator
						+ username);
				// get the folderslist in the userReports
				if (userReportsFolder.exists()) {
					for (File file : userReportsFolder.listFiles()) {
						if (file.isDirectory()) {
							// remove images
							File userimages = new File(mainFolder
									+ File.separator + "images"
									+ File.separator + file.getName());
							if (userimages.exists())
								deleteDirectory(userimages);
							// remove directory
							deleteDirectory(file);

						}
					}
				}
			}
			return true;
		} catch (Exception e) {
			// TODO: handle exception
			return false;
		}

	}

}

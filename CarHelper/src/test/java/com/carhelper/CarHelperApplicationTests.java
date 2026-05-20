package com.carhelper;

import com.carhelper.adapter.ApiAdapter;
import com.carhelper.ai.DeepSeekAiService;
import com.carhelper.ai.GeminiAiService;
import com.carhelper.ai.GroqAiService;
import com.carhelper.ai.ImageAiService;
import com.carhelper.ai.TextAiService;
import com.carhelper.chain.ImageAiAnalysisHandler;
import com.carhelper.chain.ImageDiagnosisChain;
import com.carhelper.chain.ImageDiagnosisRequest;
import com.carhelper.chain.ImageFileValidationHandler;
import com.carhelper.chain.ImageSizeValidationHandler;
import com.carhelper.command.ImageDiagnosisCommand;
import com.carhelper.command.RepairCostCommand;
import com.carhelper.command.ResaleValueCommand;
import com.carhelper.controller.AuthController;
import com.carhelper.controller.CarController;
import com.carhelper.controller.ImageDiagnosisController;
import com.carhelper.controller.ProfileController;
import com.carhelper.controller.RepairCostController;
import com.carhelper.controller.ResaleValueController;
import com.carhelper.controller.TestController;
import com.carhelper.dto.AuthRequest;
import com.carhelper.dto.RepairCostRequest;
import com.carhelper.dto.ResaleValueRequest;
import com.carhelper.dto.ResetPasswordRequest;
import com.carhelper.factory.Car;
import com.carhelper.factory.CarFactory;
import com.carhelper.factory.HyundaiCar;
import com.carhelper.factory.KiaCar;
import com.carhelper.factory.ToyotaCar;
import com.carhelper.model.CarCategory;
import com.carhelper.model.DiagnosticReport;
import com.carhelper.model.RepairCostRecord;
import com.carhelper.model.SearchHistory;
import com.carhelper.model.User;
import com.carhelper.repository.SearchHistoryRepository;
import com.carhelper.repository.UserRepository;
import com.carhelper.service.AuthService;
import com.carhelper.service.HistoryService;
import com.carhelper.service.ImageDiagnosisService;
import com.carhelper.service.RepairCostService;
import com.carhelper.service.ResaleValueService;
import com.carhelper.strategy.ElectricalRepairCostStrategy;
import com.carhelper.strategy.EngineRepairCostStrategy;
import com.carhelper.strategy.FullHighConditionResaleStrategy;
import com.carhelper.strategy.GeneralRepairCostStrategy;
import com.carhelper.strategy.HalfFullConditionResaleStrategy;
import com.carhelper.strategy.PaintRepairCostStrategy;
import com.carhelper.strategy.RepairCostEstimatorContext;
import com.carhelper.strategy.ResaleValueCalculatorContext;
import com.carhelper.strategy.ResaleValueStrategy;
import com.carhelper.strategy.StandardConditionResaleStrategy;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class CarHelperApplicationTests {

	@Test
	void applicationClassCanBeCreated() {
		CarHelperApplication application = new CarHelperApplication();
		assertNotNull(application);
	}

	@Test
	void factoryCreatesCarsCorrectly() {
		Car toyota = CarFactory.createCar("Toyota", "Camry", 2022, CarCategory.STANDARD, 85000);
		Car hyundai = CarFactory.createCar("Hyundai", "Sonata", 2021, CarCategory.HALF_FULL, 65000);
		Car kia = CarFactory.createCar("Kia", "K5", 2023, CarCategory.FULL, 90000);

		assertEquals("Toyota", toyota.getBrandName());
		assertEquals("Camry", toyota.getModel());
		assertEquals(2022, toyota.getYear());
		assertEquals(CarCategory.STANDARD, toyota.getCategory());
		assertEquals(85000, toyota.getBasePrice());
		assertEquals("Toyota standard diagnosis system", toyota.getDiagnosisGuide());

		assertEquals("Hyundai", hyundai.getBrandName());
		assertEquals("Sonata", hyundai.getModel());
		assertEquals(2021, hyundai.getYear());
		assertEquals(CarCategory.HALF_FULL, hyundai.getCategory());
		assertEquals(65000, hyundai.getBasePrice());
		assertEquals("Hyundai standard diagnosis system", hyundai.getDiagnosisGuide());

		assertEquals("Kia", kia.getBrandName());
		assertEquals("K5", kia.getModel());
		assertEquals(2023, kia.getYear());
		assertEquals(CarCategory.FULL, kia.getCategory());
		assertEquals(90000, kia.getBasePrice());
		assertEquals("Kia standard diagnosis system", kia.getDiagnosisGuide());
	}

	@Test
	void factoryRejectsInvalidBrand() {
		assertThrows(IllegalArgumentException.class, () -> CarFactory.createCar(null, "X", 2020, CarCategory.STANDARD, 1));
		assertThrows(IllegalArgumentException.class, () -> CarFactory.createCar("BMW", "X5", 2020, CarCategory.STANDARD, 1));
	}

	@Test
	void directCarClassesWork() {
		ToyotaCar toyota = new ToyotaCar("Corolla", 2020, CarCategory.STANDARD, 50000);
		HyundaiCar hyundai = new HyundaiCar("Elantra", 2020, CarCategory.HALF_FULL, 45000);
		KiaCar kia = new KiaCar("Sportage", 2020, CarCategory.FULL, 70000);

		assertEquals("Toyota", toyota.getBrandName());
		assertEquals("Hyundai", hyundai.getBrandName());
		assertEquals("Kia", kia.getBrandName());

		assertEquals("Corolla", toyota.getModel());
		assertEquals("Elantra", hyundai.getModel());
		assertEquals("Sportage", kia.getModel());

		assertEquals(50000, toyota.getBasePrice());
		assertEquals(45000, hyundai.getBasePrice());
		assertEquals(70000, kia.getBasePrice());
	}

	@Test
	void diagnosticReportWorks() {
		DiagnosticReport report = new DiagnosticReport();
		report.setIssueName("Brake Issue");
		report.setDetectedProblems("Noise");
		report.setRepairSuggestion("Check pads");
		report.setEstimatedCost("SAR 300");
		report.setAiDisclaimer("AI only");

		assertEquals("Brake Issue", report.getIssueName());
		assertEquals("Noise", report.getDetectedProblems());
		assertEquals("Check pads", report.getRepairSuggestion());
		assertEquals("SAR 300", report.getEstimatedCost());
		assertEquals("AI only", report.getAiDisclaimer());

		DiagnosticReport numeric = new DiagnosticReport("Engine", 1200.0, "Check mechanic");
		assertEquals("Engine", numeric.getIssueName());
		assertEquals("Engine", numeric.getDetectedProblems());
		assertEquals("Please visit a trusted mechanic for confirmation.", numeric.getRepairSuggestion());
		assertEquals("1200.0", numeric.getEstimatedCost());
		assertEquals("Check mechanic", numeric.getAiDisclaimer());

		DiagnosticReport full = new DiagnosticReport("Battery", "Weak battery", "Replace battery", "SAR 450", "AI");
		assertEquals("Battery", full.getIssueName());
		assertEquals("Weak battery", full.getDetectedProblems());
		assertEquals("Replace battery", full.getRepairSuggestion());
		assertEquals("SAR 450", full.getEstimatedCost());
		assertEquals("AI", full.getAiDisclaimer());
	}

	@Test
	void dtoClassesWork() {
		AuthRequest auth = new AuthRequest();
		auth.setUsername("mohand");
		auth.setEmail("test@example.com");
		auth.setPassword("pass12345");

		assertEquals("mohand", auth.getUsername());
		assertEquals("test@example.com", auth.getEmail());
		assertEquals("pass12345", auth.getPassword());

		RepairCostRequest repair = repairRequest("engine", "en");
		assertEquals(1L, repair.getUserId());
		assertEquals("Toyota", repair.getBrand());
		assertEquals("Camry", repair.getModel());
		assertEquals(2020, repair.getYear());
		assertEquals("engine", repair.getRepairType());
		assertEquals("noise", repair.getSymptoms());
		assertEquals("en", repair.getLanguage());

		ResaleValueRequest resale = resaleRequest("standard", "ar");
		assertEquals(2L, resale.getUserId());
		assertEquals("Hyundai", resale.getBrand());
		assertEquals("Sonata", resale.getModel());
		assertEquals(2021, resale.getYear());
		assertEquals(80000, resale.getMileage());
		assertEquals("standard", resale.getCondition());
		assertEquals("none", resale.getMechanicalProblems());
		assertEquals("ar", resale.getLanguage());

		ResetPasswordRequest reset = new ResetPasswordRequest();
		reset.setEmail("a@b.com");
		reset.setNewPassword("newpass123");

		assertEquals("a@b.com", reset.getEmail());
		assertEquals("newpass123", reset.getNewPassword());
	}

	@Test
	void entityClassesWork() {
		User user = new User("user", "user@example.com", "password");
		user.setId(5L);
		user.setUsername("updated");
		user.setEmail("updated@example.com");
		user.setPassword("newpass");

		SearchHistory history = new SearchHistory("Feature", "Input", "Result", user);
		history.setId(10L);
		history.setFeatureName("Repair");
		history.setInputText("input text");
		history.setResultText("result text");
		LocalDateTime now = LocalDateTime.now();
		history.setCreatedAt(now);
		history.setUser(user);

		user.setHistoryList(List.of(history));

		assertEquals(5L, user.getId());
		assertEquals("updated", user.getUsername());
		assertEquals("updated@example.com", user.getEmail());
		assertEquals("newpass", user.getPassword());
		assertEquals(1, user.getHistoryList().size());

		assertEquals(10L, history.getId());
		assertEquals("Repair", history.getFeatureName());
		assertEquals("input text", history.getInputText());
		assertEquals("result text", history.getResultText());
		assertEquals(now, history.getCreatedAt());
		assertEquals(user, history.getUser());

		RepairCostRecord record = new RepairCostRecord("Toyota", "Camry", "2020", "Noise", "SAR 500");
		record.setCarBrand("Kia");
		record.setCarModel("K5");
		record.setCarYear("2022");
		record.setProblemDescription("Smoke");
		record.setAiResult("SAR 1000");
		record.setCreatedAt(now);

		assertNull(record.getId());
		assertEquals("Kia", record.getCarBrand());
		assertEquals("K5", record.getCarModel());
		assertEquals("2022", record.getCarYear());
		assertEquals("Smoke", record.getProblemDescription());
		assertEquals("SAR 1000", record.getAiResult());
		assertEquals(now, record.getCreatedAt());
	}

	@Test
	void apiAdapterHandlesDifferentResponses() {
		ApiAdapter adapter = new ApiAdapter();

		DiagnosticReport empty = adapter.translateResponse("");
		assertEquals("AI Report", empty.getIssueName());
		assertTrue(empty.getDetectedProblems().contains("No AI response"));

		DiagnosticReport plain = adapter.translateResponse("plain answer");
		assertEquals("AI Report", plain.getIssueName());
		assertEquals("plain answer", plain.getDetectedProblems());

		String cleanJson = """
                {
                  "issueName": "Battery",
                  "detectedProblems": ["Weak battery", "Low voltage"],
                  "repairSuggestion": "Replace battery",
                  "estimatedCost": "SAR 400",
                  "aiDisclaimer": "AI only"
                }
                """;

		DiagnosticReport json = adapter.translateResponse(cleanJson);
		assertEquals("Battery", json.getIssueName());
		assertTrue(json.getDetectedProblems().contains("1. Weak battery"));
		assertTrue(json.getDetectedProblems().contains("2. Low voltage"));
		assertEquals("Replace battery", json.getRepairSuggestion());
		assertEquals("SAR 400", json.getEstimatedCost());
		assertEquals("AI only", json.getAiDisclaimer());

		String gemini = """
                {
                  "candidates": [
                    {
                      "content": {
                        "parts": [
                          {
                            "text": "{\\"issueName\\":\\"Engine\\",\\"detectedProblems\\":\\"Overheating\\",\\"repairSuggestion\\":\\"Check coolant\\",\\"estimatedCost\\":\\"SAR 700\\",\\"aiDisclaimer\\":\\"AI\\"}"
                          }
                        ]
                      }
                    }
                  ]
                }
                """;

		DiagnosticReport geminiReport = adapter.translateResponse(gemini);
		assertEquals("Engine", geminiReport.getIssueName());

		String groq = """
                {
                  "choices": [
                    {
                      "message": {
                        "content": "{\\"issueName\\":\\"Tire\\",\\"detectedProblems\\":\\"Low pressure\\",\\"repairSuggestion\\":\\"Inflate tire\\",\\"estimatedCost\\":\\"SAR 20\\",\\"aiDisclaimer\\":\\"AI\\"}"
                      }
                    }
                  ]
                }
                """;

		DiagnosticReport groqReport = adapter.translateResponse(groq);
		assertEquals("Tire", groqReport.getIssueName());

		DiagnosticReport bad = adapter.translateResponse("{bad json");
		assertEquals("AI Report", bad.getIssueName());
	}

	@Test
	void imageValidationHandlersWork() {
		ImageFileValidationHandler fileHandler = new ImageFileValidationHandler();
		ImageSizeValidationHandler sizeHandler = new ImageSizeValidationHandler();

		MockMultipartFile valid = new MockMultipartFile("file", "car.png", "image/png", "abc".getBytes());
		ImageDiagnosisRequest validRequest = new ImageDiagnosisRequest(valid, "en");

		assertDoesNotThrow(() -> fileHandler.handle(validRequest));
		assertDoesNotThrow(() -> sizeHandler.handle(validRequest));

		MockMultipartFile empty = new MockMultipartFile("file", "empty.png", "image/png", new byte[0]);
		assertThrows(IllegalArgumentException.class, () -> fileHandler.handle(new ImageDiagnosisRequest(empty, "en")));

		MockMultipartFile text = new MockMultipartFile("file", "file.txt", "text/plain", "abc".getBytes());
		assertThrows(IllegalArgumentException.class, () -> fileHandler.handle(new ImageDiagnosisRequest(text, "en")));

		MockMultipartFile noType = new MockMultipartFile("file", "file", null, "abc".getBytes());
		assertThrows(IllegalArgumentException.class, () -> fileHandler.handle(new ImageDiagnosisRequest(noType, "en")));

		byte[] largeBytes = new byte[5 * 1024 * 1024 + 1];
		MockMultipartFile large = new MockMultipartFile("file", "large.png", "image/png", largeBytes);
		assertThrows(IllegalArgumentException.class, () -> sizeHandler.handle(new ImageDiagnosisRequest(large, "en")));
	}

	@Test
	void imageAiHandlerChainServiceAndCommandWork() {
		MockMultipartFile file = new MockMultipartFile("file", "car.png", "image/png", "abc".getBytes());

		FakeGeminiAiService gemini = new FakeGeminiAiService();
		gemini.imageResult = """
                {
                  "issueName": "Damage",
                  "detectedProblems": "Scratch",
                  "repairSuggestion": "Paint",
                  "estimatedCost": "SAR 300",
                  "aiDisclaimer": "AI"
                }
                """;

		ImageAiService imageAiService = new ImageAiService(gemini);
		ApiAdapter adapter = new ApiAdapter();
		ImageAiAnalysisHandler aiHandler = new ImageAiAnalysisHandler(imageAiService, adapter);

		ImageDiagnosisRequest request = new ImageDiagnosisRequest(file, "en");
		aiHandler.handle(request);

		assertEquals("Damage", request.getReport().getIssueName());
		assertTrue(request.getRawAiResponse().contains("Damage"));

		ImageDiagnosisChain chain = new ImageDiagnosisChain(new ImageFileValidationHandler(), new ImageSizeValidationHandler(), aiHandler);
		DiagnosticReport report = chain.process(file, "en");

		assertEquals("Damage", report.getIssueName());

		ImageDiagnosisService service = new ImageDiagnosisService(chain);
		ImageDiagnosisCommand command = new ImageDiagnosisCommand(service, file, "en");

		assertEquals("Damage", service.analyze(file, "en").getIssueName());
		assertEquals("Damage", command.execute().getIssueName());
	}

	@Test
	void imageAiServiceDelegatesToGemini() {
		FakeGeminiAiService gemini = new FakeGeminiAiService();
		gemini.imageResult = "image result";

		ImageAiService imageAiService = new ImageAiService(gemini);
		MultipartFile file = new MockMultipartFile("file", "car.png", "image/png", "abc".getBytes());

		assertEquals("image result", imageAiService.analyzeImage(file));
		assertEquals("image result", imageAiService.analyzeImage(file, "Arabic"));
		assertEquals("Arabic", gemini.lastImageLanguage);
	}

	@Test
	void textAiServiceRoutesProvidersCorrectly() {
		FakeGroqAiService groq = new FakeGroqAiService();
		FakeDeepSeekAiService deepSeek = new FakeDeepSeekAiService();
		FakeGeminiAiService gemini = new FakeGeminiAiService();

		TextAiService service = new TextAiService(groq, deepSeek, gemini);

		gemini.textResult = "gemini arabic";
		assertEquals("gemini arabic", service.analyzeText("اكتب تقرير", "ar"));

		groq.textResult = "groq ok";
		assertEquals("groq ok", service.analyzeText("english prompt", "en"));

		groq.textResult = "quota limit reached";
		deepSeek.textResult = "deepseek ok";
		assertEquals("deepseek ok", service.analyzeText("fallback prompt", "en"));

		groq.textResult = "api key missing";
		deepSeek.textResult = "failed";
		gemini.textResult = "gemini ok";
		assertEquals("gemini ok", service.analyzeText("final fallback", "en"));

		gemini.textResult = "arabic by word";
		assertEquals("arabic by word", service.analyzeText("write in Arabic"));

		groq.textResult = "ok null";
		assertEquals("ok null", service.analyzeText(null, "en"));
	}

	@Test
	void aiServicesReturnSafeMessagesWhenKeysAreMissing() {
		GeminiAiService gemini = new GeminiAiService();
		GroqAiService groq = new GroqAiService();
		DeepSeekAiService deepSeek = new DeepSeekAiService();
		MultipartFile file = new MockMultipartFile("file", "car.png", "image/png", "abc".getBytes());

		assertTrue(gemini.analyzeText("hello").contains("Gemini API key is missing"));
		assertTrue(gemini.analyzeImage(file, "en").contains("Gemini API key is missing"));
		assertTrue(groq.analyzeText("hello").contains("Groq API key is missing"));
		assertTrue(groq.analyzeImage(file, "en").contains("Groq is used for text only"));
		assertTrue(deepSeek.analyzeText("hello").contains("DeepSeek API key"));
	}

	@Test
	void repairStrategiesAndContextWork() {
		RepairCostRequest engineRequest = repairRequest("engine", "en");
		RepairCostRequest paintRequest = repairRequest("paint body", "ar");
		RepairCostRequest electricalRequest = repairRequest("electrical", "en");
		RepairCostRequest generalRequest = repairRequest("tires", "en");
		RepairCostRequest nullRequest = repairRequest(null, "en");

		EngineRepairCostStrategy engine = new EngineRepairCostStrategy();
		PaintRepairCostStrategy paint = new PaintRepairCostStrategy();
		ElectricalRepairCostStrategy electrical = new ElectricalRepairCostStrategy();
		GeneralRepairCostStrategy general = new GeneralRepairCostStrategy();

		assertTrue(engine.buildPrompt(engineRequest).toLowerCase().contains("engine"));
		assertTrue(paint.buildPrompt(paintRequest).toLowerCase().contains("paint"));
		assertTrue(electrical.buildPrompt(electricalRequest).toLowerCase().contains("electrical"));
		assertTrue(general.buildPrompt(generalRequest).toLowerCase().contains("general"));

		RepairCostEstimatorContext context = new RepairCostEstimatorContext(engine, paint, electrical, general);

		assertTrue(context.buildPrompt(engineRequest).toLowerCase().contains("engine"));
		assertTrue(context.buildPrompt(paintRequest).toLowerCase().contains("paint"));
		assertTrue(context.buildPrompt(electricalRequest).toLowerCase().contains("electrical"));
		assertTrue(context.buildPrompt(generalRequest).toLowerCase().contains("general"));
		assertTrue(context.buildPrompt(nullRequest).toLowerCase().contains("general"));
	}

	@Test
	void resaleStrategiesAndContextWork() {
		StandardConditionResaleStrategy standard = new StandardConditionResaleStrategy();
		HalfFullConditionResaleStrategy halfFull = new HalfFullConditionResaleStrategy();
		FullHighConditionResaleStrategy full = new FullHighConditionResaleStrategy();

		ResaleValueRequest standardRequest = resaleRequest("standard", "en");
		ResaleValueRequest halfRequest = resaleRequest("half full", "ar");
		ResaleValueRequest fullRequest = resaleRequest("full", "en");
		ResaleValueRequest unknownRequest = resaleRequest("damaged", "en");

		assertDoesNotThrow(() -> standard.supports("standard"));
		assertDoesNotThrow(() -> standard.supports(null));
		assertDoesNotThrow(() -> halfFull.supports("half full"));
		assertDoesNotThrow(() -> halfFull.supports("standard"));
		assertDoesNotThrow(() -> full.supports("full"));
		assertDoesNotThrow(() -> full.supports("high"));
		assertDoesNotThrow(() -> full.supports("full high"));
		assertDoesNotThrow(() -> full.supports("standard"));

		String standardPrompt = standard.buildPrompt(standardRequest);
		String halfPrompt = halfFull.buildPrompt(halfRequest);
		String fullPrompt = full.buildPrompt(fullRequest);

		assertNotNull(standardPrompt);
		assertNotNull(halfPrompt);
		assertNotNull(fullPrompt);

		List<ResaleValueStrategy> strategies = List.of(standard, halfFull, full);
		ResaleValueCalculatorContext context = new ResaleValueCalculatorContext(strategies);

		String contextStandardPrompt = context.buildPrompt(standardRequest);
		String contextHalfPrompt = context.buildPrompt(halfRequest);
		String contextFullPrompt = context.buildPrompt(fullRequest);
		String contextUnknownPrompt = context.buildPrompt(unknownRequest);

		assertNotNull(contextStandardPrompt);
		assertNotNull(contextHalfPrompt);
		assertNotNull(contextFullPrompt);
		assertNotNull(contextUnknownPrompt);

		assertDoesNotThrow(() -> context.buildPrompt(standardRequest));
		assertDoesNotThrow(() -> context.buildPrompt(halfRequest));
		assertDoesNotThrow(() -> context.buildPrompt(fullRequest));
		assertDoesNotThrow(() -> context.buildPrompt(unknownRequest));
	}

	@Test
	void repairCostServiceAndCommandWork() {
		FakeTextAiService textAiService = new FakeTextAiService();
		RepairCostEstimatorContext context = new RepairCostEstimatorContext(
				new EngineRepairCostStrategy(),
				new PaintRepairCostStrategy(),
				new ElectricalRepairCostStrategy(),
				new GeneralRepairCostStrategy()
		);

		RepairCostRequest request = repairRequest("engine", "en");
		textAiService.result = "### **Estimated Repair Cost:** S A R 500 - S A R 900\n---\nProblem Summary: test\nPossible Causes: 1. one 2. two 3. three\nRecommendation: fix";

		RepairCostService service = new RepairCostService(textAiService, context);
		String result = service.estimate(request);

		assertFalse(result.contains("###"));
		assertFalse(result.contains("**"));
		assertFalse(result.contains("---"));
		assertTrue(result.contains("Estimated Repair Cost:"));
		assertTrue(result.contains("SAR"));

		RepairCostCommand command = new RepairCostCommand(service, request);
		assertEquals(result, command.execute());

		textAiService.result = null;
		assertEquals("", service.estimate(request));
	}

	@Test
	void resaleValueServiceAndCommandWork() {
		FakeTextAiService textAiService = new FakeTextAiService();
		ResaleValueCalculatorContext context = new ResaleValueCalculatorContext(List.of(
				new StandardConditionResaleStrategy(),
				new HalfFullConditionResaleStrategy(),
				new FullHighConditionResaleStrategy()
		));

		ResaleValueRequest request = resaleRequest("standard", "en");
		textAiService.result = "## **Estimated Price:** S A R 20000 - S A R 25000\n---\nCondition Summary: good\nMain Reasons: 1. clean 2. low mileage 3. market\nAdvice: sell";

		ResaleValueService service = new ResaleValueService(textAiService, context);
		String result = service.estimate(request);

		assertFalse(result.contains("##"));
		assertFalse(result.contains("**"));
		assertFalse(result.contains("---"));
		assertTrue(result.contains("Estimated Price:"));
		assertTrue(result.contains("SAR"));

		ResaleValueCommand command = new ResaleValueCommand(service, request);
		assertEquals(result, command.execute());

		textAiService.result = null;
		assertEquals("", service.estimate(request));
	}

	@Test
	void authServiceAndAuthControllerWorkWithFakeUserRepository() {
		FakeUserRepository userRepository = new FakeUserRepository();
		AuthService authService = new AuthService(userRepository);
		AuthController controller = new AuthController(authService);

		AuthRequest request = new AuthRequest();
		request.setUsername("test");
		request.setEmail("test@example.com");
		request.setPassword("pass12345");

		User registered = authService.register(request);
		assertEquals(1L, registered.getId());
		assertEquals("test", registered.getUsername());

		User logged = authService.login(request);
		assertEquals("test@example.com", logged.getEmail());

		ResponseEntity<?> loginResponse = controller.login(request);
		assertEquals(200, loginResponse.getStatusCode().value());

		Map<?, ?> loginBody = (Map<?, ?>) loginResponse.getBody();
		assertEquals("Login successful", loginBody.get("message"));
		assertEquals(1L, loginBody.get("id"));

		ResponseEntity<?> duplicate = controller.register(request);
		assertEquals(400, duplicate.getStatusCode().value());

		AuthRequest missing = new AuthRequest();
		ResponseEntity<?> missingResponse = controller.register(missing);
		assertEquals(400, missingResponse.getStatusCode().value());

		AuthRequest wrong = new AuthRequest();
		wrong.setEmail("test@example.com");
		wrong.setPassword("wrong");

		ResponseEntity<?> badLogin = controller.login(wrong);
		assertEquals(400, badLogin.getStatusCode().value());

		AuthRequest none = new AuthRequest();
		none.setEmail("none@example.com");
		none.setPassword("pass");

		ResponseEntity<?> notFoundLogin = controller.login(none);
		assertEquals(400, notFoundLogin.getStatusCode().value());
	}

	@Test
	void historyServiceWorksWithFakeRepositories() {
		FakeUserRepository userRepository = new FakeUserRepository();
		FakeSearchHistoryRepository historyRepository = new FakeSearchHistoryRepository();

		HistoryService service = new HistoryService(historyRepository, userRepository);

		service.saveHistory(null, "Feature", "Input", "Result");
		assertEquals(0, historyRepository.saved.size());

		service.saveHistory(99L, "Feature", "Input", "Result");
		assertEquals(0, historyRepository.saved.size());

		User user = new User("user", "email@test.com", "pass");
		userRepository.save(user);

		service.saveHistory(user.getId(), "Repair", "input text", "result text");

		assertEquals(1, historyRepository.saved.size());
		assertEquals("Repair", historyRepository.saved.get(0).getFeatureName());
		assertEquals("input text", historyRepository.saved.get(0).getInputText());
		assertEquals("result text", historyRepository.saved.get(0).getResultText());
		assertEquals(user, historyRepository.saved.get(0).getUser());

		List<SearchHistory> list = service.getHistoryByUserId(user.getId());
		assertEquals(1, list.size());
	}

	@Test
	void controllersWorkWithRealServicesAndFakeRepositories() {
		FakeUserRepository userRepository = new FakeUserRepository();
		FakeSearchHistoryRepository historyRepository = new FakeSearchHistoryRepository();
		HistoryService historyService = new HistoryService(historyRepository, userRepository);

		User user = new User("user", "user@test.com", "pass");
		userRepository.save(user);

		FakeGeminiAiService gemini = new FakeGeminiAiService();
		gemini.imageResult = """
                {
                  "issueName": "Issue",
                  "detectedProblems": "Problem",
                  "repairSuggestion": "Fix",
                  "estimatedCost": "SAR 1",
                  "aiDisclaimer": "AI"
                }
                """;

		ImageAiService imageAiService = new ImageAiService(gemini);
		ImageAiAnalysisHandler aiHandler = new ImageAiAnalysisHandler(imageAiService, new ApiAdapter());
		ImageDiagnosisChain chain = new ImageDiagnosisChain(new ImageFileValidationHandler(), new ImageSizeValidationHandler(), aiHandler);
		ImageDiagnosisService imageService = new ImageDiagnosisService(chain);
		ImageDiagnosisController imageController = new ImageDiagnosisController(imageService, historyService);

		MultipartFile file = new MockMultipartFile("file", "car.png", "image/png", "abc".getBytes());
		DiagnosticReport imageResult = imageController.analyzeImage(file, user.getId(), "en");

		assertEquals("Issue", imageResult.getIssueName());
		assertEquals(1, historyRepository.saved.size());
		assertEquals("Image Diagnosis", historyRepository.saved.get(0).getFeatureName());

		FakeTextAiService repairText = new FakeTextAiService();
		repairText.result = "Estimated Repair Cost: SAR 500 - SAR 900";
		RepairCostService repairService = new RepairCostService(
				repairText,
				new RepairCostEstimatorContext(
						new EngineRepairCostStrategy(),
						new PaintRepairCostStrategy(),
						new ElectricalRepairCostStrategy(),
						new GeneralRepairCostStrategy()
				)
		);
		RepairCostController repairController = new RepairCostController(repairService, historyService);
		RepairCostRequest repairRequest = repairRequest("engine", "en");
		repairRequest.setUserId(user.getId());

		String repairResult = repairController.estimate(repairRequest);
		assertTrue(repairResult.contains("Estimated Repair Cost"));
		assertEquals(2, historyRepository.saved.size());
		assertEquals("Repair Cost", historyRepository.saved.get(1).getFeatureName());

		FakeTextAiService resaleText = new FakeTextAiService();
		resaleText.result = "Estimated Price: SAR 20000 - SAR 25000";
		ResaleValueService resaleService = new ResaleValueService(
				resaleText,
				new ResaleValueCalculatorContext(List.of(
						new StandardConditionResaleStrategy(),
						new HalfFullConditionResaleStrategy(),
						new FullHighConditionResaleStrategy()
				))
		);
		ResaleValueController resaleController = new ResaleValueController(resaleService, historyService);
		ResaleValueRequest resaleRequest = resaleRequest("standard", "en");
		resaleRequest.setUserId(user.getId());

		String resaleResult = resaleController.estimate(resaleRequest);
		assertTrue(resaleResult.contains("Estimated Price"));
		assertEquals(3, historyRepository.saved.size());
		assertEquals("Car Valuation", historyRepository.saved.get(2).getFeatureName());

		ProfileController profileController = new ProfileController(historyService);
		assertEquals(3, profileController.getHistory(user.getId()).size());
	}

	@Test
	void carControllerAndTestControllerWork() {
		CarController carController = new CarController();

		ResponseEntity<?> carOk = carController.getCarDetails("Toyota", "Camry", 2020, "STANDARD");
		assertEquals(200, carOk.getStatusCode().value());

		Map<?, ?> body = (Map<?, ?>) carOk.getBody();
		assertEquals("Toyota", body.get("brand"));
		assertEquals("Camry", body.get("model"));

		ResponseEntity<?> carBadBrand = carController.getCarDetails("BMW", "X5", 2020, "STANDARD");
		assertEquals(400, carBadBrand.getStatusCode().value());

		ResponseEntity<?> carBadCategory = carController.getCarDetails("Toyota", "Camry", 2020, "wrong");
		assertEquals(400, carBadCategory.getStatusCode().value());

		TestController testController = new TestController();
		assertEquals("CarHelper running", testController.hello());
	}


	private RepairCostRequest repairRequest(String repairType, String language) {
		RepairCostRequest request = new RepairCostRequest();
		request.setUserId(1L);
		request.setBrand("Toyota");
		request.setModel("Camry");
		request.setYear(2020);
		request.setRepairType(repairType);
		request.setSymptoms("noise");
		request.setLanguage(language);
		return request;
	}

	private ResaleValueRequest resaleRequest(String condition, String language) {
		ResaleValueRequest request = new ResaleValueRequest();
		request.setUserId(2L);
		request.setBrand("Hyundai");
		request.setModel("Sonata");
		request.setYear(2021);
		request.setMileage(80000);
		request.setCondition(condition);
		request.setMechanicalProblems("none");
		request.setLanguage(language);
		return request;
	}

	static class FakeGeminiAiService extends GeminiAiService {
		String textResult = "gemini result";
		String imageResult = "image result";
		String lastImageLanguage;

		@Override
		public String analyzeText(String prompt) {
			return textResult;
		}

		@Override
		public String analyzeImage(MultipartFile file, String language) {
			lastImageLanguage = language;
			return imageResult;
		}
	}

	static class FakeGroqAiService extends GroqAiService {
		String textResult = "groq result";

		@Override
		public String analyzeText(String prompt) {
			return textResult;
		}
	}

	static class FakeDeepSeekAiService extends DeepSeekAiService {
		String textResult = "deepseek result";

		@Override
		public String analyzeText(String prompt) {
			return textResult;
		}
	}

	static class FakeTextAiService extends TextAiService {
		String result = "AI result";
		String lastPrompt;
		String lastLanguage;

		FakeTextAiService() {
			super(new GroqAiService(), new DeepSeekAiService(), new GeminiAiService());
		}

		@Override
		public String analyzeText(String prompt, String language) {
			lastPrompt = prompt;
			lastLanguage = language;
			return result;
		}

		@Override
		public String analyzeText(String prompt) {
			lastPrompt = prompt;
			return result;
		}
	}

	static class FakeUserRepository implements UserRepository {
		private final List<User> users = new ArrayList<>();
		private long nextId = 1L;

		@Override
		public Optional<User> findByEmail(String email) {
			return users.stream()
					.filter(user -> user.getEmail() != null && user.getEmail().equals(email))
					.findFirst();
		}

		@Override
		public boolean existsByEmail(String email) {
			return findByEmail(email).isPresent();
		}

		@Override
		public Optional<User> findById(Long id) {
			return users.stream()
					.filter(user -> user.getId() != null && user.getId().equals(id))
					.findFirst();
		}

		@Override
		public <S extends User> S save(S entity) {
			if (entity.getId() == null) {
				entity.setId(nextId++);
			}
			users.removeIf(user -> user.getId().equals(entity.getId()));
			users.add(entity);
			return entity;
		}

		@Override
		public List<User> findAll() {
			return users;
		}

		@Override
		public List<User> findAll(Sort sort) {
			return users;
		}

		@Override
		public Page<User> findAll(Pageable pageable) {
			return null;
		}

		@Override
		public List<User> findAllById(Iterable<Long> ids) {
			List<User> result = new ArrayList<>();
			for (Long id : ids) {
				findById(id).ifPresent(result::add);
			}
			return result;
		}

		@Override
		public long count() {
			return users.size();
		}

		@Override
		public void deleteById(Long id) {
			users.removeIf(user -> user.getId().equals(id));
		}

		@Override
		public void delete(User entity) {
			users.remove(entity);
		}

		@Override
		public void deleteAllById(Iterable<? extends Long> ids) {
			for (Long id : ids) {
				deleteById(id);
			}
		}

		@Override
		public void deleteAll(Iterable<? extends User> entities) {
			for (User user : entities) {
				delete(user);
			}
		}

		@Override
		public void deleteAll() {
			users.clear();
		}

		@Override
		public boolean existsById(Long id) {
			return findById(id).isPresent();
		}

		@Override
		public <S extends User> List<S> saveAll(Iterable<S> entities) {
			List<S> result = new ArrayList<>();
			for (S entity : entities) {
				result.add(save(entity));
			}
			return result;
		}

		@Override
		public void flush() {
		}

		@Override
		public <S extends User> S saveAndFlush(S entity) {
			return save(entity);
		}

		@Override
		public <S extends User> List<S> saveAllAndFlush(Iterable<S> entities) {
			return saveAll(entities);
		}

		@Override
		public void deleteAllInBatch(Iterable<User> entities) {
			deleteAll(entities);
		}

		@Override
		public void deleteAllByIdInBatch(Iterable<Long> ids) {
			deleteAllById(ids);
		}

		@Override
		public void deleteAllInBatch() {
			deleteAll();
		}

		@Override
		public User getOne(Long id) {
			return findById(id).orElse(null);
		}

		@Override
		public User getById(Long id) {
			return findById(id).orElse(null);
		}

		@Override
		public User getReferenceById(Long id) {
			return findById(id).orElse(null);
		}

		@Override
		public <S extends User> Optional<S> findOne(Example<S> example) {
			return Optional.empty();
		}

		@Override
		public <S extends User> List<S> findAll(Example<S> example) {
			return new ArrayList<>();
		}

		@Override
		public <S extends User> List<S> findAll(Example<S> example, Sort sort) {
			return new ArrayList<>();
		}

		@Override
		public <S extends User> Page<S> findAll(Example<S> example, Pageable pageable) {
			return null;
		}

		@Override
		public <S extends User> long count(Example<S> example) {
			return 0;
		}

		@Override
		public <S extends User> boolean exists(Example<S> example) {
			return false;
		}

		@Override
		public <S extends User, R> R findBy(Example<S> example, java.util.function.Function<org.springframework.data.repository.query.FluentQuery.FetchableFluentQuery<S>, R> queryFunction) {
			return null;
		}
	}

	static class FakeSearchHistoryRepository implements SearchHistoryRepository {
		final List<SearchHistory> saved = new ArrayList<>();
		private long nextId = 1L;

		@Override
		public List<SearchHistory> findByUserIdOrderByCreatedAtDesc(Long userId) {
			return saved.stream()
					.filter(history -> history.getUser() != null)
					.filter(history -> history.getUser().getId() != null)
					.filter(history -> history.getUser().getId().equals(userId))
					.toList();
		}

		@Override
		public Optional<SearchHistory> findById(Long id) {
			return saved.stream()
					.filter(history -> history.getId() != null && history.getId().equals(id))
					.findFirst();
		}

		@Override
		public <S extends SearchHistory> S save(S entity) {
			if (entity.getId() == null) {
				entity.setId(nextId++);
			}
			saved.removeIf(history -> history.getId().equals(entity.getId()));
			saved.add(entity);
			return entity;
		}

		@Override
		public List<SearchHistory> findAll() {
			return saved;
		}

		@Override
		public List<SearchHistory> findAll(Sort sort) {
			return saved;
		}

		@Override
		public Page<SearchHistory> findAll(Pageable pageable) {
			return null;
		}

		@Override
		public List<SearchHistory> findAllById(Iterable<Long> ids) {
			List<SearchHistory> result = new ArrayList<>();
			for (Long id : ids) {
				findById(id).ifPresent(result::add);
			}
			return result;
		}

		@Override
		public long count() {
			return saved.size();
		}

		@Override
		public void deleteById(Long id) {
			saved.removeIf(history -> history.getId().equals(id));
		}

		@Override
		public void delete(SearchHistory entity) {
			saved.remove(entity);
		}

		@Override
		public void deleteAllById(Iterable<? extends Long> ids) {
			for (Long id : ids) {
				deleteById(id);
			}
		}

		@Override
		public void deleteAll(Iterable<? extends SearchHistory> entities) {
			for (SearchHistory history : entities) {
				delete(history);
			}
		}

		@Override
		public void deleteAll() {
			saved.clear();
		}

		@Override
		public boolean existsById(Long id) {
			return findById(id).isPresent();
		}

		@Override
		public <S extends SearchHistory> List<S> saveAll(Iterable<S> entities) {
			List<S> result = new ArrayList<>();
			for (S entity : entities) {
				result.add(save(entity));
			}
			return result;
		}

		@Override
		public void flush() {
		}

		@Override
		public <S extends SearchHistory> S saveAndFlush(S entity) {
			return save(entity);
		}

		@Override
		public <S extends SearchHistory> List<S> saveAllAndFlush(Iterable<S> entities) {
			return saveAll(entities);
		}

		@Override
		public void deleteAllInBatch(Iterable<SearchHistory> entities) {
			deleteAll(entities);
		}

		@Override
		public void deleteAllByIdInBatch(Iterable<Long> ids) {
			deleteAllById(ids);
		}

		@Override
		public void deleteAllInBatch() {
			deleteAll();
		}

		@Override
		public SearchHistory getOne(Long id) {
			return findById(id).orElse(null);
		}

		@Override
		public SearchHistory getById(Long id) {
			return findById(id).orElse(null);
		}

		@Override
		public SearchHistory getReferenceById(Long id) {
			return findById(id).orElse(null);
		}

		@Override
		public <S extends SearchHistory> Optional<S> findOne(Example<S> example) {
			return Optional.empty();
		}

		@Override
		public <S extends SearchHistory> List<S> findAll(Example<S> example) {
			return new ArrayList<>();
		}

		@Override
		public <S extends SearchHistory> List<S> findAll(Example<S> example, Sort sort) {
			return new ArrayList<>();
		}

		@Override
		public <S extends SearchHistory> Page<S> findAll(Example<S> example, Pageable pageable) {
			return null;
		}

		@Override
		public <S extends SearchHistory> long count(Example<S> example) {
			return 0;
		}

		@Override
		public <S extends SearchHistory> boolean exists(Example<S> example) {
			return false;
		}

		@Override
		public <S extends SearchHistory, R> R findBy(Example<S> example, java.util.function.Function<org.springframework.data.repository.query.FluentQuery.FetchableFluentQuery<S>, R> queryFunction) {
			return null;
		}
	}
}
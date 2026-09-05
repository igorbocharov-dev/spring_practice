package com.practice.spring.integration.controller;

public class DocumentControllerIT {

//    @Autowired
//    private DocumentRepository documentRepository;
//
//    @Autowired
//    private Clock clock;
//    @Autowired
//    private MockMvc mockMvc;
//    @Autowired
//    private ObjectMapper objectMapper;
//    @Autowired
//    private MongoTemplate mongoTemplate;
//
//    @BeforeEach
//    void cleanUp(){
//        mongoTemplate.remove(new Query(), DocumentEntity.class);
//    }

//    @Test
//    void shouldCreateDocumentEntityAndReturnDocumentResponseWithStatusIsOk() throws Exception {
//        DocumentRequest documentRequest = DocumentFactory.documentRequest();
//        String jsonRequest = objectMapper.writeValueAsString(documentRequest);
//
//        MvcResult result = mockMvc.perform(post("/api/v1/document")
//                        .content(jsonRequest)
//                        .contentType(MediaType.APPLICATION_JSON))
//                .andExpectAll(
//                        jsonPath("$.id").exists(),
//                        jsonPath("$.body").exists(),
//                        jsonPath("$.links").exists(),
//                        jsonPath("$.status").exists(),
//                        jsonPath("$.createdAt").exists(),
//                        jsonPath("$.updatedAt").exists()
//                ).andExpect(status().isOk()).andReturn();
//
//        assertThat(documentRepository.findAll().size()).isEqualTo(1);
//
//        String jsonResult = result.getResponse().getContentAsString();
//        DocumentResponse documentResponse = objectMapper.readValue(jsonResult, DocumentResponse.class);
//
//        assertThat(documentResponse.id()).isNotNull();
//        assertThat(documentResponse.body()).isEqualTo(documentRequest.body());
//        assertThat(documentResponse.links()).containsExactlyInAnyOrderElementsOf(documentRequest.links());
//        assertThat(documentResponse.status()).isEqualTo(DocumentStatus.NEW);
//        assertThat(documentResponse.createdAt())
//                .isBetween(Instant.now(clock).minusSeconds(60), Instant.now(clock));
//        assertThat(documentResponse.updatedAt())
//                .isBetween(Instant.now(clock).minusSeconds(60), Instant.now(clock));
//    }
}

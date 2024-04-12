package com.bielcik.traveller.repository;

import com.bielcik.traveller.domain.DocumentType;
import com.bielcik.traveller.domain.Traveller;
import com.bielcik.traveller.domain.TravellerDocument;
import com.github.dockerjava.api.model.ExposedPort;
import com.github.dockerjava.api.model.PortBinding;
import com.github.dockerjava.api.model.Ports;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.context.annotation.Bean;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import javax.sql.DataSource;
import java.time.LocalDate;
import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.core.Is.is;

@DataJpaTest
@Testcontainers
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class TravellerRepositoryTest {

    private static final String EMAIL = "john@example.com";
    @Autowired
    private TravellerRepository travellerRepository;

    @Container
    @ServiceConnection
    private static final PostgreSQLContainer<?> postgresContainer = new PostgreSQLContainer<>("postgres:16")
            .withDatabaseName("test")
            .withUsername("test")
            .withPassword("test")
//            .withExposedPorts(5432)
//            .withCreateContainerCmdModifier(cmd -> {
//                cmd.getHostConfig().withPortBindings(
//                        new PortBinding(Ports.Binding.bindPort(25432),
//                                new ExposedPort(5432)));
//            })
            ;

//    @TestConfiguration
//    static class TestConfig {
//        @Bean
//        public DataSource dataSource() {
//            return DataSourceBuilder.create()
//                    .driverClassName(postgresContainer.getDriverClassName())
//                    .url(postgresContainer.getJdbcUrl())
//                    .username(postgresContainer.getUsername())
//                    .password(postgresContainer.getPassword())
//                    .build();
//        }
//    }

    @Test
//    @Transactional(propagation = Propagation.NEVER)
    public void testFindByDocumentTypeAndNumberAndIssuingCountry() {
        Traveller traveller = getTraveller();

        TravellerDocument document = getTravellerDocument(DocumentType.PASSPORT, "ABC123", "USA", true);
        TravellerDocument document2 = getTravellerDocument(DocumentType.ID_DOCUMENT, "ABC123", "USA", false);
        traveller.addDocument(document);
        traveller.addDocument(document2);

        travellerRepository.save(traveller);
        Optional<Traveller> foundTraveller = travellerRepository.findByDocumentTypeAndNumberAndIssuingCountryAndActive(DocumentType.PASSPORT, "ABC123", "USA");

        assertThat(foundTraveller.isPresent(), is(true));
        Traveller t = foundTraveller.get();
        assertThat(t.getMobileNumber(), is(traveller.getMobileNumber()));
        assertThat(t.getDateOfBirth(), is(traveller.getDateOfBirth()));
        assertThat(t.getEmail(), is(traveller.getEmail()));

        assertThat(t.getTravellerDocuments(), hasSize(2));
        assertThat(t.getTravellerDocuments().get(0).getDocumentNumber(), is(document.getDocumentNumber()));
        assertThat(t.getTravellerDocuments().get(0).getDocumentType(), is(document.getDocumentType()));
        assertThat(t.getTravellerDocuments().get(0).getDocumentIssuingCountry(), is(document.getDocumentIssuingCountry()));
    }

    @Test
    public void testFindByDocumentTypeAndNumberAndIssuingCountry_withNoActiveDocuments() {
        Traveller traveller = getTraveller();

        TravellerDocument document = getTravellerDocument(DocumentType.PASSPORT, "ABC123", "USA", false);
        TravellerDocument document2 = getTravellerDocument(DocumentType.ID_DOCUMENT, "ABC123", "USA", false);
        traveller.addDocument(document);
        traveller.addDocument(document2);

        travellerRepository.save(traveller);

        Optional<Traveller> foundTraveller = travellerRepository.findByDocumentTypeAndNumberAndIssuingCountryAndActive(DocumentType.PASSPORT, "ABC123", "USA");

        assertThat(foundTraveller.isEmpty(), is(true));
    }

    @Test
    public void testFindByDocumentTypeAndNumberAndIssuingCountry_withDeletedTrue() {
        Traveller traveller = getTraveller();
        traveller.setDeleted(true);

        TravellerDocument document = getTravellerDocument(DocumentType.PASSPORT, "ABC123", "USA", true);
        TravellerDocument document2 = getTravellerDocument(DocumentType.ID_DOCUMENT, "ABC123", "USA", false);
        traveller.addDocument(document);
        traveller.addDocument(document2);

        travellerRepository.save(traveller);

        Optional<Traveller> foundTraveller = travellerRepository.findByDocumentTypeAndNumberAndIssuingCountryAndActive(DocumentType.PASSPORT, "ABC123", "USA");

        assertThat(foundTraveller.isEmpty(), is(true));
    }

    @Test
    public void updateTraveler_removeDocument() {
        Traveller traveller = getTraveller();

        TravellerDocument document = getTravellerDocument(DocumentType.PASSPORT, "ABC123", "USA", true);
        TravellerDocument document2 = getTravellerDocument(DocumentType.ID_DOCUMENT, "ABC123", "USA", false);
        traveller.addDocument(document);
        traveller.addDocument(document2);

        travellerRepository.save(traveller);

        Optional<Traveller> foundTraveller = travellerRepository.findByDocumentTypeAndNumberAndIssuingCountryAndActive(DocumentType.PASSPORT, "ABC123", "USA");

        assertThat(foundTraveller.isPresent(), is(true));

        traveller.getTravellerDocuments().remove(1);
        travellerRepository.save(traveller);
        Optional<Traveller> updatedTraveller = travellerRepository.findByDocumentTypeAndNumberAndIssuingCountryAndActive(DocumentType.PASSPORT, "ABC123", "USA");

        assertThat(updatedTraveller.isPresent(), is(true));
        assertThat(updatedTraveller.get().getTravellerDocuments().size(), is(1));
    }

    @Test
    public void updateTraveler_removeAllDocuments() {
        Traveller traveller = getTraveller();

        TravellerDocument document = getTravellerDocument(DocumentType.PASSPORT, "ABC123", "USA", true);
        TravellerDocument document2 = getTravellerDocument(DocumentType.ID_DOCUMENT, "ABC123", "USA", false);
        traveller.addDocument(document);
        traveller.addDocument(document2);

        travellerRepository.save(traveller);

        Optional<Traveller> foundTraveller = travellerRepository.findByDocumentTypeAndNumberAndIssuingCountryAndActive(DocumentType.PASSPORT, "ABC123", "USA");

        assertThat(foundTraveller.isPresent(), is(true));

        traveller.getTravellerDocuments().clear();
        travellerRepository.save(traveller);
        Optional<Traveller> updatedTraveller = travellerRepository.findByDocumentTypeAndNumberAndIssuingCountryAndActive(DocumentType.PASSPORT, "ABC123", "USA");

        assertThat(updatedTraveller.isEmpty(), is(true));

        Optional<Traveller> t = travellerRepository.findByEmailAndDeletedIsFalse(EMAIL);
        assertThat(t.isPresent(), is(true));
    }

    @Test
    public void updateTraveler_emailUpdate() {
        Traveller traveller = getTraveller();

        TravellerDocument document = getTravellerDocument(DocumentType.PASSPORT, "ABC123", "USA", true);
        TravellerDocument document2 = getTravellerDocument(DocumentType.ID_DOCUMENT, "ABC123", "USA", false);
        traveller.addDocument(document);
        traveller.addDocument(document2);

        travellerRepository.save(traveller);

        traveller.setEmail("john@john.com");
        travellerRepository.save(traveller);

        Optional<Traveller> t = travellerRepository.findByEmailAndDeletedIsFalse("john@john.com");
        assertThat(t.isPresent(), is(true));
    }

    private Traveller getTraveller() {
        Traveller traveller = new Traveller();
        traveller.setFirstName("John");
        traveller.setLastName("Doe");
        traveller.setDateOfBirth(LocalDate.of(1990, 1, 1));
        traveller.setEmail(EMAIL);
        traveller.setMobileNumber("1234567890");
        return traveller;
    }

    private TravellerDocument getTravellerDocument(DocumentType documentType, String documentNumber, String issuingCountry, boolean active) {
        TravellerDocument document = new TravellerDocument();
        document.setDocumentType(documentType);
        document.setDocumentNumber(documentNumber);
        document.setDocumentIssuingCountry(issuingCountry);
        document.setActive(active);
        return document;
    }
}
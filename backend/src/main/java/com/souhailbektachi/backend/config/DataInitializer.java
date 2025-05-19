package com.souhailbektachi.backend.config;

import com.souhailbektachi.backend.entities.*;
import com.souhailbektachi.backend.repositories.*;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import java.time.LocalDate;

@Configuration
@RequiredArgsConstructor
public class DataInitializer {

    @Bean
    @Profile("!prod")
    @Transactional
    public CommandLineRunner initData(
            ClientRepository clientRepository,
            CreditPersonnelRepository creditPersonnelRepository,
            CreditImmobilierRepository creditImmobilierRepository,
            CreditProfessionnelRepository creditProfessionnelRepository,
            RemboursementRepository remboursementRepository
    ) {
        return args -> {
            System.out.println("Initializing database with test data...");
            
            // Create clients
            Client client1 = new Client();
            client1.setNom("Souhail Bektachi");
            client1.setEmail("souhail@example.com");
            clientRepository.save(client1);
            
            Client client2 = new Client();
            client2.setNom("Ahmed Alami");
            client2.setEmail("ahmed@example.com");
            clientRepository.save(client2);
            
            // Create credit personnel for client1
            CreditPersonnel creditPersonnel = new CreditPersonnel();
            creditPersonnel.setClient(client1);
            creditPersonnel.setDateDemande(LocalDate.now().minusMonths(3));
            creditPersonnel.setStatut(StatutCredit.ACCEPTE);
            creditPersonnel.setDateAcception(LocalDate.now().minusMonths(2));
            creditPersonnel.setMontant(50000.0);
            creditPersonnel.setDureeRemboursement(36);
            creditPersonnel.setTauxInteret(3.5);
            creditPersonnel.setMotif("Achat de voiture");
            creditPersonnelRepository.save(creditPersonnel);
            
            // Create credit immobilier for client1
            CreditImmobilier creditImmobilier = new CreditImmobilier();
            creditImmobilier.setClient(client1);
            creditImmobilier.setDateDemande(LocalDate.now().minusMonths(6));
            creditImmobilier.setStatut(StatutCredit.ACCEPTE);
            creditImmobilier.setDateAcception(LocalDate.now().minusMonths(5));
            creditImmobilier.setMontant(800000.0);
            creditImmobilier.setDureeRemboursement(240);
            creditImmobilier.setTauxInteret(2.8);
            creditImmobilier.setTypeBienFinance(TypeBienImmobilier.APPARTEMENT);
            creditImmobilierRepository.save(creditImmobilier);
            
            // Create credit professionnel for client2
            CreditProfessionnel creditProfessionnel = new CreditProfessionnel();
            creditProfessionnel.setClient(client2);
            creditProfessionnel.setDateDemande(LocalDate.now().minusMonths(4));
            creditProfessionnel.setStatut(StatutCredit.EN_COURS);
            creditProfessionnel.setMontant(300000.0);
            creditProfessionnel.setDureeRemboursement(60);
            creditProfessionnel.setTauxInteret(4.2);
            creditProfessionnel.setMotif("Développement business");
            creditProfessionnel.setRaisonSocialeEntreprise("Alami Tech SARL");
            creditProfessionnelRepository.save(creditProfessionnel);
            
            // Create some remboursements for the accepted credits
            // For credit personnel
            Remboursement remb1 = new Remboursement();
            remb1.setCredit(creditPersonnel);
            remb1.setDate(LocalDate.now().minusMonths(1));
            remb1.setMontant(1451.23);
            remb1.setType(TypeRemboursement.MENSUALITE);
            remboursementRepository.save(remb1);
            
            Remboursement remb2 = new Remboursement();
            remb2.setCredit(creditPersonnel);
            remb2.setDate(LocalDate.now());
            remb2.setMontant(1451.23);
            remb2.setType(TypeRemboursement.MENSUALITE);
            remboursementRepository.save(remb2);
            
            // For credit immobilier
            Remboursement remb3 = new Remboursement();
            remb3.setCredit(creditImmobilier);
            remb3.setDate(LocalDate.now().minusMonths(4));
            remb3.setMontant(3825.67);
            remb3.setType(TypeRemboursement.MENSUALITE);
            remboursementRepository.save(remb3);
            
            Remboursement remb4 = new Remboursement();
            remb4.setCredit(creditImmobilier);
            remb4.setDate(LocalDate.now().minusMonths(3));
            remb4.setMontant(3825.67);
            remb4.setType(TypeRemboursement.MENSUALITE);
            remboursementRepository.save(remb4);
            
            Remboursement remb5 = new Remboursement();
            remb5.setCredit(creditImmobilier);
            remb5.setDate(LocalDate.now().minusMonths(2));
            remb5.setMontant(10000.0);
            remb5.setType(TypeRemboursement.REMBOURSEMENT_ANTICIPE);
            remboursementRepository.save(remb5);
            
            Remboursement remb6 = new Remboursement();
            remb6.setCredit(creditImmobilier);
            remb6.setDate(LocalDate.now().minusMonths(1));
            remb6.setMontant(3825.67);
            remb6.setType(TypeRemboursement.MENSUALITE);
            remboursementRepository.save(remb6);
            
            Remboursement remb7 = new Remboursement();
            remb7.setCredit(creditImmobilier);
            remb7.setDate(LocalDate.now());
            remb7.setMontant(3825.67);
            remb7.setType(TypeRemboursement.MENSUALITE);
            remboursementRepository.save(remb7);
            
            System.out.println("Test data initialization completed!");
        };
    }
}

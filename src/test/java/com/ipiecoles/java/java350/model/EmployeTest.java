package com.ipiecoles.java.java350.model;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.time.LocalDate;

public class EmployeTest {

    @Test
    public void testGetNombreAnneeEncienneteDateEmbaucheNow(){
        //Given
        Employe employe = new Employe();
        employe.setDateEmbauche(LocalDate.now());

        //When
        Integer nbAnneesAnciennete = employe.getNombreAnneeAnciennete();

        //Then
        Assertions.assertThat(nbAnneesAnciennete).isZero();
    }

    @Test
    public void testGetNbAnneesAncienneteDateEmbauchePassee(){
        //Given
        //Date d'embauche 10 ans dans le passé
        Employe employe = new Employe();
        employe.setDateEmbauche(LocalDate.now().minusYears(10));
        //employe.setDateEmbauche(LocalDate.of(2012, 4, 26)); //Pas bon...
        //When
        Integer nbAnneesAnciennete = employe.getNombreAnneeAnciennete();
        //Then
        // => 10
        Assertions.assertThat(nbAnneesAnciennete).isEqualTo(10);
    }

    @Test
    public void testGetNbAnneesAncienneteDateEmbaucheFuture(){
        //Given
        //Date d'embauche 2 ans dans le futur
        Employe employe = new Employe();
        employe.setDateEmbauche(LocalDate.now().plusYears(2));
        //When
        Integer nbAnneesAnciennete = employe.getNombreAnneeAnciennete();
        //Then
        // => 0
        Assertions.assertThat(nbAnneesAnciennete).isZero();
    }

    @Test
    public void testGetNbAnneesAncienneteDateEmbaucheNull(){
        //Given
        Employe employe = new Employe();
        employe.setDateEmbauche(null);
        //When
        Integer nbAnneesAnciennete = employe.getNombreAnneeAnciennete();
        //Then
        // => 0
        Assertions.assertThat(nbAnneesAnciennete).isZero();
    }

    @ParameterizedTest
    @CsvSource({
            "'M12345',0,1,1.0,1700.0",
            "'M12345',2,1,1.0,1900.0",
            "'M12345',0,2,1.0,1700.0",
            "'M12345',0,1,0.5,850.0",
            "'C12345',0,1,1.0,1000",
            "'C12345',5,1,1.0,1500",
            "'C12345',2,1,1.0,1200",
            "'C12345',0,2,1.0,2300.0",
            "'C12345',3,2,1.0,2600.0",
            "'C12345',0,1,0.5,500",
            ",0,1,1.0,1000.",
            "'C12345',0,,1.0,1000"

    })
    public void testGetPrimeAnnuelManagerPerformanceBasePleinTemps(
        String matricule,
        Integer nbAnneesAnciennete,
        Integer performance,
        Double tauxActivite,
        Double prime
    ){
        //Given
        Employe employe = new Employe("Manage","Manager",matricule,LocalDate.now().minusYears(nbAnneesAnciennete),2500d,performance,tauxActivite);

        //When
        Double primeObtenue = employe.getPrimeAnnuelle();

        //Then
        Assertions.assertThat(primeObtenue).isEqualTo(prime);
    }

    @ParameterizedTest
    @CsvSource({
            "'M12345',0,1,1.0,11",
            "'M12345',2,1,1.0,11",
            "'M12345',0,2,1.0,11",
            "'M12345',0,1,0.5,6",
            "'C12345',0,1,1.0,11",
            "'C12345',5,1,1.0,11",
            "'C12345',2,1,1.0,11",
            "'C12345',0,2,1.0,11",
            "'C12345',3,2,1.0,11",
            "'C12345',0,1,0.5,6",
            ",0,1,1.0,11",
            "'C12345',0,,1.0,11"

    })
    public void testGetNbRtt(
            String matricule,
            Integer nbAnneesAnciennete,
            Integer performance,
            Double tauxActivite,
            Integer nbRttAttendu
    ){
        //Given
        LocalDate d = LocalDate.now();
        Employe employe = new Employe("Manage","Manager",matricule,LocalDate.now().minusYears(nbAnneesAnciennete),2500d,performance,tauxActivite);

        //When
        Integer nbRtt = employe.getNbRtt(d);
        //Then
        Assertions.assertThat(nbRtt).isEqualTo(nbRttAttendu);
    }
}

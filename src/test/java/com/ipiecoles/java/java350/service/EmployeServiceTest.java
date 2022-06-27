package com.ipiecoles.java.java350.service;

import com.ipiecoles.java.java350.exception.EmployeException;
import com.ipiecoles.java.java350.model.Employe;
import com.ipiecoles.java.java350.model.Entreprise;
import com.ipiecoles.java.java350.model.NiveauEtude;
import com.ipiecoles.java.java350.model.Poste;
import com.ipiecoles.java.java350.repository.EmployeRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import javax.persistence.EntityExistsException;
import java.time.LocalDate;

@ExtendWith(MockitoExtension.class)
public class EmployeServiceTest {

    @InjectMocks
    private EmployeService employeService;

    @Mock
    private EmployeRepository employeRepository;

    @Test
    public void testEmbaucheEmployeLimiteMatricule() throws EmployeException {
        //Given
        Mockito.when(employeRepository.findLastMatricule()).thenReturn("99999");
        //when

        Throwable t = Assertions.catchThrowable(()->{
            employeService.embaucheEmploye("Doe", "John", Poste.COMMERCIAL, NiveauEtude.MASTER, 1.0);
        });
        //then
        Assertions.assertThat(t).isInstanceOf(EmployeException.class)
                .hasMessage("Limite de 100000 matricules atteinte !");

    }
    @Test
    public void testEmbaucheEmployeExist(){
        //Given
        Mockito.when(employeRepository.findLastMatricule()).thenReturn(null);
        Mockito.when(employeRepository.findByMatricule("COOOO1")).thenReturn(new Employe());

        //when

        Throwable t = Assertions.catchThrowable(()->{
            employeService.embaucheEmploye("Doe", "John", Poste.COMMERCIAL, NiveauEtude.MASTER, 1.0);
        });
        //then
        Assertions.assertThat(t).isInstanceOf(EntityExistsException.class)
                .hasMessage("L'employé avec le matricule C000001 existe déjà");


    }

    @Test
    public void EmployeServiceEmbauchePartiel() throws EmployeException{
        //Given
        Mockito.when(employeRepository.findLastMatricule()).thenReturn("12345");
        Mockito.when(employeRepository.findByMatricule("C12346")).thenReturn(null);
        //When
        // avec Mockito

        employeService.embaucheEmploye("Doe", "John",Poste.COMMERCIAL, NiveauEtude.MASTER,1.0);

        //Then
        Employe employe = employeRepository.findByMatricule("12345");
        Assertions.assertThat(employe.getNom()).isEqualTo("Doe");
        Assertions.assertThat(employe.getPrenom()).isEqualTo("John");
        Assertions.assertThat(employe.getMatricule()).isEqualTo("C12346");
        Assertions.assertThat(employe.getDateEmbauche()).isEqualTo(LocalDate.now());
        Assertions.assertThat(employe.getPerformance()).isEqualTo(Entreprise.PERFORMANCE_BASE);
        Assertions.assertThat(employe.getSalaire()).isEqualTo(1064.85);
        Assertions.assertThat(employe.getTempsPartiel()).isEqualTo(0.5);

    }

    @Test
    public void EmployeServiceEmbauche() throws EmployeException{
        //Given
        Mockito.when(employeRepository.findLastMatricule()).thenReturn(null);
        //When
        employeService.embaucheEmploye("Doe", "John",Poste.COMMERCIAL, NiveauEtude.MASTER,1.0);
        // avec Mockito


        //Then
        ArgumentCaptor<Employe> employeArgumentCaptor = ArgumentCaptor.forClass(Employe.class);
        Mockito.verify(employeRepository).save(employeArgumentCaptor.capture());

        Employe employe = employeArgumentCaptor.getValue();
        Assertions.assertThat(employe).isNotNull();
        Assertions.assertThat(employe.getPrenom()).isEqualTo("John");
        Assertions.assertThat(employe.getNom()).isEqualTo("Doe");
        Assertions.assertThat(employe.getMatricule()).isEqualTo("C00001");
        Assertions.assertThat(employe.getPerformance()).isEqualTo(1);
        Assertions.assertThat(employe.getTempsPartiel()).isEqualTo(1.0);
        Assertions.assertThat(employe.getDateEmbauche()).isEqualTo(LocalDate.now());
        Assertions.assertThat(employe.getSalaire()).isEqualTo(2129.71);

    }




}


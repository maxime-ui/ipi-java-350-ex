package com.ipiecoles.java.java350.model;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.Objects;

@Entity
public class Employe {


    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String nom;

    private String prenom;

    private String matricule;

    private LocalDate dateEmbauche;

    private Double salaire = Entreprise.SALAIRE_BASE;

    private Integer performance = Entreprise.PERFORMANCE_BASE;

    private Double tempsPartiel = 1.0;

    public String toString() {
        return "Employe{" +
                "id=" + id +
                ", nom='" + nom + '\'' +
                ", prenom='" + prenom + '\'' +
                ", matricule='" + matricule + '\'' +
                ", dateEmbauche=" + dateEmbauche +
                ", salaire=" + salaire +
                ", performance=" + performance +
                ", tempsPartiel=" + tempsPartiel +
                '}';
    }

    public Employe() {
    }

    public Employe(String nom, String prenom, String matricule, LocalDate dateEmbauche, Double salaire, Integer performance, Double tempsPartiel) {
        this.nom = nom;
        this.prenom = prenom;
        this.matricule = matricule;
        this.dateEmbauche = dateEmbauche;
        this.salaire = salaire;
        this.performance = performance;
        this.tempsPartiel = tempsPartiel;
    }

    /**
     * Méthode calculant le nombre d'années d'ancienneté à partir de la date d'embauche
     * @return
     */
    public Integer getNombreAnneeAnciennete() {
        return LocalDate.now().getYear() - dateEmbauche.getYear();
    }

    public Integer getNbConges() {
        return Entreprise.NB_CONGES_BASE + this.getNombreAnneeAnciennete();
    }

    public Integer getNbRtt(){
        return getNbRtt(LocalDate.now());
    }
    /**
     *
     * @param dateActuel
     * @return
     */
    //renommer variables pour rendre plus compréhensible et faire la java doc indenter ajouter le break manquant
    public Integer getNbRtt(LocalDate dateActuel){
        int NbrDeJourAnnee = dateActuel.isLeapYear() ? 366 : 365;
        //somme de tous les samedi et dimanche dans l'anée
        int NbrJourWeekdend = 104;
        //on recupère le jour du 1 Janvier de l'année de dateActuel
        switch (LocalDate.of(dateActuel.getYear(),1,1).getDayOfWeek()){
            case THURSDAY:
                //si date actuel est une année bissextile et que c'est un Jeudi
                if(dateActuel.isLeapYear())
                    NbrJourWeekdend =  NbrJourWeekdend + 1;
                break;
            //si date actuel est une année bissextile et que c'est un Vendredi
            case FRIDAY:
                if(dateActuel.isLeapYear()) NbrJourWeekdend =  NbrJourWeekdend + 2;
                else NbrJourWeekdend =  NbrJourWeekdend + 1;
                break;
            //si date actuel est une année bissextile et que c'est un Samedi
            case SATURDAY:
                NbrJourWeekdend = NbrJourWeekdend + 1;
                break;

            default :
                break;
        }

        int NbrDeJoursFeries = (int) Entreprise.joursFeries(dateActuel).stream().filter(localDate ->
                localDate.getDayOfWeek().getValue() <= DayOfWeek.FRIDAY.getValue()).count();
        return (int) Math.ceil((NbrDeJourAnnee - Entreprise.NB_JOURS_MAX_FORFAIT - NbrJourWeekdend - Entreprise.NB_CONGES_BASE - NbrDeJoursFeries) * tempsPartiel);
    }

    /**
     * Calcul de la prime annuelle selon la règle :
     * Pour les managers : Prime annuelle de base bonnifiée par l'indice prime manager
     * Pour les autres employés, la prime de base plus éventuellement la prime de performance calculée si l'employé
     * n'a pas la performance de base, en multipliant la prime de base par un l'indice de performance
     * (égal à la performance à laquelle on ajoute l'indice de prime de base)
     *
     * Pour tous les employés, une prime supplémentaire d'ancienneté est ajoutée en multipliant le nombre d'année
     * d'ancienneté avec la prime d'ancienneté. La prime est calculée au pro rata du temps de travail de l'employé
     *
     * @return la prime annuelle de l'employé en Euros et cents
     */
    //Matricule, performance, date d'embauche, temps partiel, prime
    public Double getPrimeAnnuelle(){
        //Calcule de la prime d'ancienneté
        Double primeAnciennete = Entreprise.PRIME_ANCIENNETE * this.getNombreAnneeAnciennete();
        Double prime;
        //Prime du manager (matricule commençant par M) : Prime annuelle de base multipliée par l'indice prime manager
        //plus la prime d'anciennté.
        if(matricule != null && matricule.startsWith("M")) {
            prime = Entreprise.primeAnnuelleBase() * Entreprise.INDICE_PRIME_MANAGER + primeAnciennete;
        }
        //Pour les autres employés en performance de base, uniquement la prime annuelle plus la prime d'ancienneté.
        else if (this.performance == null || Entreprise.PERFORMANCE_BASE.equals(this.performance)){
            prime = Entreprise.primeAnnuelleBase() + primeAnciennete;
        }
        //Pour les employés plus performance, on bonnifie la prime de base en multipliant par la performance de l'employé
        // et l'indice de prime de base.
        else {
            prime = Entreprise.primeAnnuelleBase() * (this.performance + Entreprise.INDICE_PRIME_BASE) + primeAnciennete;
        }
        //Au pro rata du temps partiel.
        return prime * this.tempsPartiel;
    }

    //Augmenter salaire
    //ne pas changer la signature de la méthode
    private double augmenterSalaire(double pourcentage) throws EmployeException {
        if(pourcentage >= 0 ){
            salaire += (salaire * pourcentage/100);
        }else{
            throw new EmployeException(" pourcentage inférieur à 0 donc c'est une diminution du salaire ");
        }

        return salaire;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    /**
     * @return the nom
     */
    public String getNom() {
        return nom;
    }

    /**
     * @param nom the nom to set
     */
    public Employe setNom(String nom) {
        this.nom = nom;
        return this;
    }

    /**
     * @return the prenom
     */
    public String getPrenom() {
        return prenom;
    }

    /**
     * @param prenom the prenom to set
     */
    public void setPrenom(String prenom) {
        this.prenom = prenom;
    }

    /**
     * @return the matricule
     */
    public String getMatricule() {
        return matricule;
    }

    /**
     * @param matricule the matricule to set
     */
    public void setMatricule(String matricule) {
        this.matricule = matricule;
    }

    /**
     * @return the dateEmbauche
     */
    public LocalDate getDateEmbauche() {
        return dateEmbauche;
    }

    /**
     * @param dateEmbauche the dateEmbauche to set
     */
    public void setDateEmbauche(LocalDate dateEmbauche) {
        this.dateEmbauche = dateEmbauche;
    }

    /**
     * @return the salaire
     */
    public Double getSalaire() {
        return salaire;
    }

    /**
     * @param salaire the salaire to set
     */
    public void setSalaire(Double salaire) {
        this.salaire = salaire;
    }

    public Integer getPerformance() {
        return performance;
    }

    public void setPerformance(Integer performance) {
        this.performance = performance;
    }

    public Double getTempsPartiel() {
        return tempsPartiel;
    }

    public void setTempsPartiel(Double tempsPartiel) {
        this.tempsPartiel = tempsPartiel;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Employe)) return false;
        Employe employe = (Employe) o;
        return Objects.equals(id, employe.id) &&
                Objects.equals(nom, employe.nom) &&
                Objects.equals(prenom, employe.prenom) &&
                Objects.equals(matricule, employe.matricule) &&
                Objects.equals(dateEmbauche, employe.dateEmbauche) &&
                Objects.equals(salaire, employe.salaire) &&
                Objects.equals(performance, employe.performance);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, nom, prenom, matricule, dateEmbauche, salaire, performance);
    }
}

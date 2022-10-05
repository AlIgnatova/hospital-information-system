package course.spring.hospitalinformationsystem.entity.enums;

public enum DischargeDisposition {
    IMPROVED("With improvement"),
    SAME("With no change in disposition"),
    WORSE("In worse disposition"),
    DEAD("Deceased"),
    NOT_DISCHARGED("");


    private String dischargeDisposition;

    DischargeDisposition(String dischargeDisposition) {
        this.dischargeDisposition = dischargeDisposition;
    }

    public String getDischargeDisposition() {
        return dischargeDisposition;
    }
}

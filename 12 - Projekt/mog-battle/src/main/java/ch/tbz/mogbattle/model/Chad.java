package ch.tbz.mogbattle.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

@Entity
public class Chad {

    @Id
    @GeneratedValue
    private Long id;

    @NotBlank
    private String name;

    @Min(1) @Max(10)
    private int height;

    @Min(1) @Max(10)
    private int jawline;

    @Min(1) @Max(10)
    private int aura;

    @Min(1) @Max(10)
    private int status;

    @Min(1) @Max(10)
    private int frame;

    protected Chad() {
    }

    public Chad(String name, int height, int jawline, int aura, int status, int frame) {
        this.name = name;
        this.height = height;
        this.jawline = jawline;
        this.aura = aura;
        this.status = status;
        this.frame = frame;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public int getHeight() {
        return height;
    }

    public int getJawline() {
        return jawline;
    }

    public int getAura() {
        return aura;
    }

    public int getStatus() {
        return status;
    }

    public int getFrame() {
        return frame;
    }
}

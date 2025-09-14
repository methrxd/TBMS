package moviebookingsystem;

import java.io.Serializable;

class Movie implements Serializable {
    private String name;
    private int duration;
    private String screeningTime;
    private String language;
    private String format;
    private String displayLanguage;
    private String poster;

    public Movie(String name, int duration, String screeningTime, String language, String format, String displayLanguage, String poster) {
        this.name = name;
        this.duration = duration;
        this.screeningTime = screeningTime;
        this.language = language;
        this.format = format;
        this.displayLanguage = displayLanguage;
        this.poster = poster;
    }

    public String getName() {
        return name;
    }

    public int getDuration() {
        return duration;
    }

    public String getScreeningTime() {
        return screeningTime;
    }

    public String getLanguage() {
        return language;
    }

    public String getFormat() {
        return format;
    }

    public String getDisplayLanguage() {
        return displayLanguage;
    }

    public String getPoster() {
        return poster;
    }

    @Override
    public String toString() {
        return "Movie{" +
                "name='" + name + '\'' +
                ", duration=" + duration +
                ", screeningTime='" + screeningTime + '\'' +
                ", language='" + language + '\'' +
                ", format='" + format + '\'' +
                ", displayLanguage='" + displayLanguage + '\'' +
                ", poster='" + poster + '\'' +
                '}';
    }
}
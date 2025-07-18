package com.niquewrld.tutorspace.models;

import java.util.HashMap;
import java.util.Map;

public class Data {

    public static final String[] PROGRAMMES = {
            "DPINF1", "MICMT1", "BICTH1", "BINCT1",
            "ADICT1", "DIIAF1" , "(4 year ECP)", "DIIAD1"
    };

    public static final String[] MODULES = {
            "ICTL101",
            "BFND101",
            "APDA101",
            "FCSC101",
            "OSYS101",
            "INSS101",
            "MWMU101",
            "CSTN101",
            "APDP101",
            "APDB101",
            "CNTW101",
            "BFND201",
            "MCPA201",
            "ISYA201",
            "ITPM101",
            "INMA201",
            "CMEP101",
            "MCPB201",
            "ISYB201",
            "APDB201",
            "INMB201",
            "APDP201",
            "APDA301",
            "ISYA301",
            "ADPA301",
            "HCIN101",
            "TIPP301",
            "ENSP101",
            "APDB301",
            "ISYB301",
            "ADPB301",
            "DAST401",
            "PBDE401",
            "RESK401",
            "APMC401",
            "SODM401",
            "APMC402",
            "SAMA301",
            "BUIN301",
            "PDCO301",
            "MAIN301",
            "GRAP301",
            "HCIN301",
            "INCP101",
            "DSTR101",
            "ICMS101",
            "MCMA101",
            "SWDF101",
            "MCMB101",
            "SYSF101",
            "SADS201",
            "LWLF101",
            "OGBH201",
            "NOPS201",
            "PRLN201",
            "ALDS201",
            "INFM201",
            "INAS201",
            "COAR201",
            "IEXP101",
            "PBDV301",
            "IPRT301",
            "SPRI301",
            "PRJA301",
            "PRJB301",
            "SAQM301",
            "SFEN301",
            "PJMN301",
            "BSIT301",
            "PDCP301",
            "MCHI301",
            "GRPH301",
            "HCPI301",
            "WSYT301",
            "ADDA401",
            "ASDM401",
            "CLCO401",
            "MALE402",
            "PRESE4R",
            "WMSD401",
            "VSSE401",
            "ADCY402",
            "ADIP402",
            "ADNT401",
            "APRE402",
            "AUED402",
            "GLPP402",
            "INTG402"
    };

    public static final String[] MAIN_CATEGORIES = {
            "Programming", "Databases", "Networking", "Security",
            "Maths", "IoT", "AI", "Projects", "Library",
            "Business", "Soft Skills"
    };

    public static final Map<String, String[]> SUB_CATEGORIES_MAP = new HashMap<String, String[]>() {{
        put("DPINF1", new String[]{
                "FCSC101",
                "OSYS101",
                "ICTL101",
                "ILGA101",
                "SKDA101",
                "ILGB101",
                "Software Development & Management"
        });
        put("Databases", new String[]{
                "Database Administration",
                "Data Management",
                "Business Intelligence"
        });
        put("Networking", new String[]{
                "Communications Networks",
                "Computer Organization & Architecture",
                "Networking",
                "Networks & Operating Systems"
        });
        put("Security", new String[]{
                "Fundamentals of Computer Security",
                "Information Assurance & Security",
                "Theory of ICT Professional Practice"
        });
        put("Maths", new String[]{
                "Engineering Mathematics (1A, 1B)",
                "Discrete Structures",
                "Applied Math for Computing",
                "IT Logic & Technology"
        });
        put("IoT", new String[]{
                "IoT & Embedded Systems",
                "IoT Data Analysis",
                "Sensor Principles & Technology",
                "Embedded Microcontroller Technology",
                "Cloud Computing"
        });
        put("AI", new String[]{
                "Basic AI Applications",
                "Machine Intelligence"
        });
        put("Projects", new String[]{
                "IT Project Management",
                "Business Analysis Projects (I–III)",
                "IoT Project Planning & Implementation"
        });
        put("Library", new String[]{
                "Library & Information Practice (1–3)",
                "Knowledge Management",
                "Library Marketing & Promotion",
                "Library Professional Practice"
        });
        put("Business", new String[]{
                "Business Fundamentals (I–II)",
                "Entrepreneurial Spirit",
                "Business Process Engineering",
                "Organizational Behavior"
        });
        put("Soft Skills", new String[]{
                "Communication in English/Zulu",
                "Cornerstone 101",
                "Law for Life",
                "Human Computer Interaction"
        });
    }};
}
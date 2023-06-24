package com.droidev.geradorderecibo;

import android.content.Context;

import java.io.File;

public class Miscs {

    public void deleteCache(Context context) {
        try {
            File dir = context.getCacheDir();
            deleteDir(dir);
        } catch (Exception ignored) {
        }
    }

    private static boolean deleteDir(File dir) {
        if (dir != null && dir.isDirectory()) {
            String[] children = dir.list();
            assert children != null;
            for (String child : children) {
                boolean success = deleteDir(new File(dir, child));
                if (!success) {
                    return false;
                }
            }
            return dir.delete();
        } else if (dir != null && dir.isFile()) {
            return dir.delete();
        } else {
            return false;
        }
    }

    public String dataHoje(String data) {

        data = data.replace("/01/", " de Janeiro de ")
                .replace("/02/", " de Fevereiro de ")
                .replace("/03/", " de Março de ")
                .replace("/04/", " de Abril de ")
                .replace("/05/", " de Maio de ")
                .replace("/06/", " de Junho de ")
                .replace("/07/", " de Julho de ")
                .replace("/08/", " de Agosto de ")
                .replace("/09/", " de Setembro de ")
                .replace("/10/", " de Outubro de ")
                .replace("/11/", " de Novembro de ")
                .replace("/12/", " de Dezembro de ");

        return "Natal - RN, " + data + ".";
    }
}

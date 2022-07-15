package com.andrei.signatureauthentication;

import android.support.annotation.Nullable;

import java.io.*;

public class ManagerIO {

    public static void deleteSignatures(File fileDir) {
        for (int i = 1; i <= 5; i++) {
            new File(fileDir, "S" + Integer.toString(i) + ".txt").delete();
        }
    }

    public static void saveSignature(Signature s, File fileDir) {
        try {
            ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(new File(fileDir, "S" + Integer.toString(s.getId()) + ".txt")));
            oos.writeObject(s);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void saveUserData(UserData ud, File fileDir, String username) {
        try {
            ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(new File(fileDir, username + ".txt")));
            oos.writeObject(ud);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static Signature loadSignature(File fileDir, int id) {
        try {
            ObjectInputStream ois1 = new ObjectInputStream(new FileInputStream(new File(fileDir, "S" + Integer.toString(id) + ".txt")));
            return (Signature) ois1.readObject();
        } catch (EOFException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException cnf) {
            cnf.printStackTrace();
        }
        return null;
    }

    public static UserData loadUserData(File fileDir, String username) {
        try {
            ObjectInputStream ois1 = new ObjectInputStream(new FileInputStream(new File(fileDir, username + ".txt")));
            return (UserData) ois1.readObject();
        } catch (EOFException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException cnf) {
            cnf.printStackTrace();
        }
        return null;
    }
}

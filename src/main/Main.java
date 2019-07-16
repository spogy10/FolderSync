package main;

import exceptions.SaveStatusException;
import exceptions.SetupStatusException;
import exceptions.StatusNotIntializedException;
import model.Status;

public class Main {


    public static void main(String[] args) {

        try {
            if(!Status.setUpStatus())
                throw new SetupStatusException();
        } catch (SetupStatusException e) {
            e.printStackTrace();
            System.exit(1);
        }


        try {
            if(!Status.saveStatus())
                throw new SaveStatusException();
        } catch (StatusNotIntializedException e) {
            e.printStackTrace();
        } catch (SaveStatusException e) {
            e.printStackTrace();
        }
    }
}

package com.solvetech.homeagent.model;

import android.content.Context;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by wpy on 10/24/15.
 */
public class ModelCacheHelper implements Serializable {

    Context context;

    public ModelCacheHelper(Context context) {
        this.context = context;
    }

    public void cacheCustomers(List<CustomerSummary> customers) throws IOException {
        persistLocal(customers, "customers.obj");
    }

    public ArrayList<CustomerSummary> fetchCustomers() throws ClassNotFoundException {
        try {
            return (ArrayList<CustomerSummary>) getLocalPersistance("customers.obj");
        } catch (IOException e) {
            e.printStackTrace();
            return new ArrayList<CustomerSummary>();
        }
    }

    public void cacheProjects(List<ProjectSummary> projects) throws IOException {
        persistLocal(projects, "projects.obj");
    }

    public ArrayList<ProjectSummary> fetchProjects() throws ClassNotFoundException {
        try {
            return (ArrayList<ProjectSummary>) getLocalPersistance("projects.obj");
        } catch (IOException e) {
            e.printStackTrace();
            return new ArrayList<ProjectSummary>();
        }
    }

    private void persistLocal(Object obj, String filename) throws IOException {
        FileOutputStream fos = context.openFileOutput(filename, Context.MODE_PRIVATE);
        ObjectOutputStream os = new ObjectOutputStream(fos);
        os.writeObject(obj);
        os.close();
        fos.close();
    }

    private Object getLocalPersistance(String filename) throws IOException, ClassNotFoundException {
        FileInputStream fis = context.openFileInput(filename);
        ObjectInputStream is = new ObjectInputStream(fis);
        Object data = is.readObject();
        is.close();
        fis.close();
        return data;
    }
}

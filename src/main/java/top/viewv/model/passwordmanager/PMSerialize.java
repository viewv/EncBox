package top.viewv.model.passwordmanager;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;

public class PMSerialize {
    public void serialize(PMStorage pmStorage, String destfilename) throws FileNotFoundException {
        Kryo kryo = new Kryo();
        kryo.register(PMStorage.class);

        Output output = new Output(new FileOutputStream(destfilename));
        kryo.writeObject(output, pmStorage);
        output.close();
    }

    public PMStorage deserialize(String sourcefilename) throws FileNotFoundException {
        Kryo kryo = new Kryo();
        kryo.register(PMStorage.class);

        Input input = new Input(new FileInputStream(sourcefilename));
        PMStorage pmStorage = kryo.readObject(input, PMStorage.class);
        input.close();
        return pmStorage;
    }
}

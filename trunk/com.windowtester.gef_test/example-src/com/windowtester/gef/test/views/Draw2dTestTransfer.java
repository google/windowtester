package com.windowtester.gef.test.views;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import org.eclipse.swt.dnd.ByteArrayTransfer;
import org.eclipse.swt.dnd.TransferData;


public class Draw2dTestTransfer extends ByteArrayTransfer
{
    private static final String TYPE_NAME = Draw2dTestTransfer.class.getName();
    private static final int TYPE_ID = registerType( TYPE_NAME );
    private static Draw2dTestTransfer _instance = new Draw2dTestTransfer();
    
    /** sigleton accessor */
    public static Draw2dTestTransfer getInstance()
    {
        return _instance;
    }
    
    @Override
    protected String[] getTypeNames()
    {
        return new String[] { TYPE_NAME };
    }

    @Override
    protected int[] getTypeIds()
    {
        return new int[] { TYPE_ID };
    }
    
    /**
     * serialize the transfer object
     */
    @Override
    public void javaToNative(Object object, TransferData transferData )
    {
        try
        {
            ObjectOutputStream writeOut = null;
            try
            {
                // write data to a byte array and then ask super to convert to pMedium
                ByteArrayOutputStream out = new ByteArrayOutputStream();
                writeOut = new ObjectOutputStream(out);
                writeOut.writeObject(object);
                byte[] byteBuffer = out.toByteArray();
                super.javaToNative(byteBuffer, transferData);
            }

            finally
            {
                if(writeOut != null)
                {
                    writeOut.close();
                }
            }

        }
        catch(IOException e)
        {
            e.printStackTrace();
        }
    }
    
    /**
     * deserialize the data transfer object
     */
    @Override
    public Object nativeToJava( TransferData transferData )
    {
        if(isSupportedType(transferData))
        {
            byte[] buffer = (byte[]) super.nativeToJava(transferData);
            if(buffer == null)
                return null;
            try
            {
                ObjectInputStream ois = null;
                try
                {
                    ByteArrayInputStream in = new ByteArrayInputStream(buffer);
                    ois = new ObjectInputStream(in);
                    return ois.readObject();
                }
                finally
                {
                    if(ois != null)
                        ois.close();
                }

            }
            catch(Exception iox)
            {
                iox.printStackTrace();
                return null;

            }
        }
        return null;
    }

}

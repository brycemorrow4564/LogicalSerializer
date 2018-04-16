package valueSerializer.base;

import java.io.NotSerializableException;
import java.io.StreamCorruptedException;
import java.nio.ByteBuffer;
import java.util.ArrayList;

import general.AStringBuffer;
import general.SerializerRegistry;
import util.trace.port.serialization.extensible.ExtensibleBufferDeserializationFinished;
import util.trace.port.serialization.extensible.ExtensibleBufferDeserializationInitiated;
import util.trace.port.serialization.extensible.ExtensibleValueSerializationFinished;
import util.trace.port.serialization.extensible.ExtensibleValueSerializationInitiated;
import valueSerializer.ValueSerializer;

public class FloatSerializer implements ValueSerializer {

	@Override
	public void objectToBuffer(Object anOutputBuffer, Object anObject, ArrayList<Object> visitedObjects)
			throws NotSerializableException {
		if (anObject instanceof Float) {
			ExtensibleValueSerializationInitiated.newCase(this, anObject, anOutputBuffer);
			Float value = (Float) anObject; 
			Class bufferClass = anOutputBuffer.getClass(); 
			if (bufferClass == ByteBuffer.class) {
				//Binary encoding 
				ByteBuffer bBuff = (ByteBuffer) anOutputBuffer;
				bBuff.put(Float.class.getName().getBytes());
				bBuff.putFloat(value);
			} else if (bufferClass == AStringBuffer.class) {
				//Textual encoding 
				AStringBuffer sBuff = (AStringBuffer) anOutputBuffer;
				Object[] args = {Float.class.getName() + value + ValueSerializer.DELIMETER};
				sBuff.executeStringBufferMethod(SerializerRegistry.sbAppend, args);
			} else {
				throw new NotSerializableException("Buffer of unsupported type passed to Float value serializer");
			}
			ExtensibleValueSerializationFinished.newCase(this, anObject, anOutputBuffer, visitedObjects);
		} else {
			throw new NotSerializableException("Tried to serialize a non Float type with the Float value serializer");
		}
	}

	@Override
	public Object objectFromBuffer(Object anInputBuffer, Class aClass, ArrayList<Object> retrievedObjects)
			throws StreamCorruptedException, NotSerializableException {
		Float retVal = null; 
		if (aClass == Float.class) {
			ExtensibleBufferDeserializationInitiated.newCase(this, null, anInputBuffer, aClass);
			if (anInputBuffer instanceof ByteBuffer) {
				//Decode from binary 
				ByteBuffer bBuff = (ByteBuffer) anInputBuffer; 
				int strLen = bBuff.getInt();
				byte[] strBytes = new byte[strLen]; 
				bBuff.get(strBytes);
				retVal = Float.parseFloat(new String(strBytes));
			} else if (anInputBuffer instanceof AStringBuffer) {
				//Decode from textual representation 
				AStringBuffer sBuff = (AStringBuffer) anInputBuffer;
				String c = null;
				StringBuilder sb = new StringBuilder(); 
				while (true) {
					c = sBuff.readCharacter(); 
					if (c == null) {
						throw new NotSerializableException("Unexpectedly reached end of StringBuffer");
					} else if (c.equals(ValueSerializer.DELIMETER)) {
						break; 
					} else {
						sb.append(c);
					}
				}
				retVal = Float.parseFloat(sb.toString());
			} else {
				throw new NotSerializableException("Buffer of unsupported type passed to Float value serializer");
			}
			ExtensibleBufferDeserializationFinished.newCase(this, null, anInputBuffer, retVal, retrievedObjects);
			return retVal;  
		} else {
			throw new NotSerializableException("Tried to deserialize a non Float type with the Float value serialzer");
		}
	}

}
package valueSerializer.base;

import java.io.NotSerializableException;
import java.io.StreamCorruptedException;
import java.nio.ByteBuffer;
import java.util.ArrayList;

import general.AStringBuffer;
import general.SerializerRegistry;
import general.TypeIndependentSerializer;
import util.annotations.Comp533Tags;
import util.annotations.Tags;
import util.trace.port.serialization.extensible.ExtensibleBufferDeserializationFinished;
import util.trace.port.serialization.extensible.ExtensibleBufferDeserializationInitiated;
import util.trace.port.serialization.extensible.ExtensibleValueSerializationFinished;
import util.trace.port.serialization.extensible.ExtensibleValueSerializationInitiated;
import valueSerializer.ValueSerializer;

@Tags({Comp533Tags.NULL_SERIALIZER})
public class NullSerializer implements ValueSerializer {

	@Override
	public void objectToBuffer(Object anOutputBuffer, Object anObject, ArrayList<Object> visitedObjects)
			throws NotSerializableException {
		if (anObject == null) {
			ExtensibleValueSerializationInitiated.newCase(this, anObject, anOutputBuffer);
			Class bufferClass = anOutputBuffer.getClass(); 
			if (ByteBuffer.class.isAssignableFrom(bufferClass)) {
				//Binary encoding 
				ByteBuffer bBuff = (ByteBuffer) anOutputBuffer;
				bBuff.put(SerializerRegistry.NULL_HEADER.getBytes());
				bBuff.put(SerializerRegistry.NULL_VALUE.getBytes());
			} else if (bufferClass == AStringBuffer.class) {
				//Textual encoding 
				AStringBuffer sBuff = (AStringBuffer) anOutputBuffer;
				String str = SerializerRegistry.NULL_HEADER + SerializerRegistry.NULL_VALUE;
				sBuff.append(str);
			} else {
				throw new NotSerializableException("Buffer of unsupported type passed to Null value serializer");
			}
			ExtensibleValueSerializationFinished.newCase(this, anObject, anOutputBuffer, visitedObjects);
		} else {
			throw new NotSerializableException("Tried to serialize a non Null type with the Null value serializer");
		}
	}

	@Override
	public Object objectFromBuffer(Object anInputBuffer, Class aClass, ArrayList<Object> retrievedObjects)
			throws StreamCorruptedException, NotSerializableException {
		String nullStr = null;			
		ExtensibleBufferDeserializationInitiated.newCase(this, null, anInputBuffer, Object.class);
		if (anInputBuffer instanceof ByteBuffer) {
			ByteBuffer bBuff = (ByteBuffer) anInputBuffer; 
			byte[] bytes = new byte[SerializerRegistry.NULL_VALUE.length()];
			bBuff.get(bytes, 0, SerializerRegistry.NULL_VALUE.length());
			nullStr = new String(bytes);
		} else if (anInputBuffer instanceof AStringBuffer) {
			AStringBuffer sBuff = (AStringBuffer) anInputBuffer; 
			nullStr = sBuff.readCharacters(SerializerRegistry.NULL_VALUE.length()); 
		} else {
			throw new NotSerializableException("Buffer of unsupported type");
		}
		if (nullStr.equals("null")) {
			ExtensibleBufferDeserializationFinished.newCase(this, null, anInputBuffer, null, retrievedObjects);
			return null;
		} else {
			throw new NotSerializableException("Expected to parse \"null\" from buffer but was unable to do so");
		}
	}
	
}

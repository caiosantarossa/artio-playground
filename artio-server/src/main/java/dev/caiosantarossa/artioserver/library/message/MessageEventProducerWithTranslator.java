package dev.caiosantarossa.artioserver.library.message;

import com.lmax.disruptor.EventTranslatorOneArg;
import com.lmax.disruptor.RingBuffer;

import java.nio.ByteBuffer;

public class MessageEventProducerWithTranslator {

    private final RingBuffer<MessageEvent> ringBuffer;

    public MessageEventProducerWithTranslator(RingBuffer<MessageEvent> ringBuffer) {
        this.ringBuffer = ringBuffer;
    }

    private static final EventTranslatorOneArg<MessageEvent, ByteBuffer> TRANSLATOR =
            (event, sequence, bb) -> event.set(new String(bb.array()));

    public void onData(ByteBuffer bb) {
        ringBuffer.publishEvent(TRANSLATOR, bb);
    }

}

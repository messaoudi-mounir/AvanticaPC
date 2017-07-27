// Copyright (c) 2007-Present Pivotal Software, Inc.  All rights reserved.
//
// This software, the RabbitMQ Java client library, is triple-licensed under the
// Mozilla Public License 1.1 ("MPL"), the GNU General Public License version 2
// ("GPL") and the Apache License version 2 ("ASL"). For the MPL, please see
// LICENSE-MPL-RabbitMQ. For the GPL, please see LICENSE-GPL2.  For the ASL,
// please see LICENSE-APACHE2.
//
// This software is distributed on an "AS IS" basis, WITHOUT WARRANTY OF ANY KIND,
// either express or implied. See the LICENSE file for specific language governing
// rights and limitations of this software.
//
// If you have any questions regarding licensing, please contact us at
// info@rabbitmq.com.

package com.rabbitmq.perf;

import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.ConfirmListener;
import com.rabbitmq.client.ReturnListener;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.UUID;
import java.util.concurrent.Semaphore;

public class Producer extends ProducerConsumerBase implements Runnable, ReturnListener,
        ConfirmListener
{
    private final Channel channel;
    private final String  exchangeName;
    private final String  id;
    private final boolean randomRoutingKey;
    private final boolean mandatory;
    private final boolean immediate;
    private final boolean persistent;
    private final int     txSize;
    private final int     msgLimit;
    private final long    timeLimit;

    private final Stats   stats;

    private final MessageBodySource messageBodySource;

    private Semaphore confirmPool;
    private final SortedSet<Long> unconfirmedSet =
        Collections.synchronizedSortedSet(new TreeSet<Long>());

    public Producer(Channel channel, String exchangeName, String id, boolean randomRoutingKey,
                    List<?> flags, int txSize,
                    float rateLimit, int msgLimit, int timeLimit,
                    long confirm, MessageBodySource messageBodySource, Stats stats)
        throws IOException {

        this.channel            = channel;
        this.exchangeName       = exchangeName;
        this.id                 = id;
        this.randomRoutingKey   = randomRoutingKey;
        this.mandatory          = flags.contains("mandatory");
        this.immediate          = flags.contains("immediate");
        this.persistent         = flags.contains("persistent");
        this.txSize             = txSize;
        this.rateLimit          = rateLimit;
        this.msgLimit           = msgLimit;
        this.timeLimit          = 1000L * timeLimit;
        this.messageBodySource = messageBodySource;
        if (confirm > 0) {
            this.confirmPool  = new Semaphore((int)confirm);
        }
        this.stats        = stats;
    }

    public void handleReturn(int replyCode,
                             String replyText,
                             String exchange,
                             String routingKey,
                             AMQP.BasicProperties properties,
                             byte[] body)
        throws IOException {
        stats.handleReturn();
    }

    public void handleAck(long seqNo, boolean multiple) {
        handleAckNack(seqNo, multiple, false);
    }

    public void handleNack(long seqNo, boolean multiple) {
        handleAckNack(seqNo, multiple, true);
    }

    private void handleAckNack(long seqNo, boolean multiple,
                               boolean nack) {
        int numConfirms = 0;
        if (multiple) {
            SortedSet<Long> confirmed = unconfirmedSet.headSet(seqNo + 1);
            numConfirms += confirmed.size();
            confirmed.clear();
        } else {
            unconfirmedSet.remove(seqNo);
            numConfirms = 1;
        }
        if (nack) {
            stats.handleNack(numConfirms);
        } else {
            stats.handleConfirm(numConfirms);
        }

        if (confirmPool != null) {
            for (int i = 0; i < numConfirms; ++i) {
                confirmPool.release();
            }
        }

    }

    public void run() {
        long now;
        long startTime;
        startTime = now = System.currentTimeMillis();
        lastStatsTime = startTime;
        msgCount = 0;
        int totalMsgCount = 0;

        try {

            while ((timeLimit == 0 || now < startTime + timeLimit) &&
                   (msgLimit == 0 || msgCount < msgLimit)) {
                delay(now);
                if (confirmPool != null) {
                    confirmPool.acquire();
                }
                publish(messageBodySource.create(totalMsgCount));
                totalMsgCount++;
                msgCount++;

                if (txSize != 0 && totalMsgCount % txSize == 0) {
                    channel.txCommit();
                }
                stats.handleSend();
                now = System.currentTimeMillis();
            }

        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (InterruptedException e) {
            throw new RuntimeException (e);
        }
    }

    private void publish(MessageBodySource.MessageBodyAndContentType messageBodyAndContentType)
        throws IOException {

        AMQP.BasicProperties.Builder propertiesBuilder = new AMQP.BasicProperties.Builder();
        if (persistent) {
            propertiesBuilder.deliveryMode(2);
        }

        if (messageBodyAndContentType.getContentType() != null) {
            propertiesBuilder.contentType(messageBodyAndContentType.getContentType());
        }

        unconfirmedSet.add(channel.getNextPublishSeqNo());
        channel.basicPublish(exchangeName, randomRoutingKey ? UUID.randomUUID().toString() : id,
                             mandatory, immediate,
                             propertiesBuilder.build(),
                             messageBodyAndContentType.getBody());
    }

}

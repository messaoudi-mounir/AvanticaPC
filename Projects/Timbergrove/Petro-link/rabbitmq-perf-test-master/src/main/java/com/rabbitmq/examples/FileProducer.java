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


package com.rabbitmq.examples;

import java.io.File;
import java.io.FileInputStream;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.GnuParser;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.AMQP.BasicProperties;

public class FileProducer {
    public static void main(String[] args) {
	Options options = new Options();
	options.addOption(new Option("h", "uri", true, "AMQP URI"));
	options.addOption(new Option("p", "port", true, "broker port"));
	options.addOption(new Option("t", "type", true, "exchange type"));
	options.addOption(new Option("e", "exchange", true, "exchange name"));
	options.addOption(new Option("k", "routing-key", true, "routing key"));

        CommandLineParser parser = new GnuParser();

        try {
            CommandLine cmd = parser.parse(options, args);

            String uri = strArg(cmd, 'h', "amqp://localhost");
	    String exchangeType = strArg(cmd, 't', "direct");
	    String exchange = strArg(cmd, 'e', null);
	    String routingKey = strArg(cmd, 'k', null);

            ConnectionFactory connFactory = new ConnectionFactory();
            connFactory.setUri(uri);
            Connection conn = connFactory.newConnection();

            final Channel ch = conn.createChannel();

	    if (exchange == null) {
		System.err.println("Please supply exchange name to send to (-e)");
		System.exit(2);
	    }
	    if (routingKey == null) {
		System.err.println("Please supply routing key to send to (-k)");
		System.exit(2);
	    }
	    ch.exchangeDeclare(exchange, exchangeType);

	    for (String filename : cmd.getArgs()) {
		System.out.print("Sending " + filename + "...");
		File f = new File(filename);
		FileInputStream i = new FileInputStream(f);
		byte[] body = new byte[(int) f.length()];
		i.read(body);
		i.close();

		Map<String, Object> headers = new HashMap<String, Object>();
		headers.put("filename", filename);
		headers.put("length", (int) f.length());
		BasicProperties props = new BasicProperties.Builder().headers(headers).build();
		ch.basicPublish(exchange, routingKey, props, body);
		System.out.println(" done.");
	    }

	    conn.close();
        } catch (Exception ex) {
            System.err.println("Main thread caught exception: " + ex);
            ex.printStackTrace();
            System.exit(1);
        }
    }

    private static String strArg(CommandLine cmd, char opt, String def) {
        return cmd.getOptionValue(opt, def);
    }
}

package ru.vyarus.gradle.plugin.animalsniffer.signature.support

import org.codehaus.mojo.animal_sniffer.Clazz

import java.util.zip.GZIPInputStream

/**
 * @author Vyacheslav Rusakov
 * @since 21.04.2017
 */
class SignatureReader {

    static List<String> readSignature(File file) {
        List<String> res = []
        new ObjectInputStream(new GZIPInputStream(new FileInputStream(file))).withCloseable {
            while (true) {
                Clazz c = (Clazz) it.readObject();
                if (c == null) {
                    break
                }
                res.add(c.getName().replace('/', '.'));
            }
        }
        return res
    }
}

package ru.vyarus.gradle.plugin.animalsniffer.info

import groovy.transform.CompileStatic
import org.codehaus.mojo.animal_sniffer.Clazz

import java.util.zip.GZIPInputStream

/**
 * Signature reader utility.
 *
 * @author Vyacheslav Rusakov
 * @since 21.04.2017
 */
@CompileStatic
class SignatureReader {

    /**
     * @param file signature file to read
     * @return list of all classes in signature
     */
    static List<String> readSignature(File file) {
        List<String> res = []
        new ObjectInputStream(new GZIPInputStream(new FileInputStream(file))).withCloseable {
            while (true) {
                Clazz c = (Clazz) it.readObject()
                if (c == null) {
                    break
                }
                res.add(c.name.replace('/', '.'))
            }
        }
        return res
    }
}

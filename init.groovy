import groovy.lang.GroovyClassLoader

//cl = new GroovyClassLoader()


// **/*.groovy encontra os arquivo tbm no nível do diretorio


//List<String> classes = new FileNameFinder().getFileNames('./',  '*/*.groovy')
//
////final GroovyClassLoader classLoader = this.class.classLoader
//final GroovyClassLoader classLoader = new GroovyClassLoader(this.class.classLoader)
//
//
//classes.each { String strClassFilePath ->
//
////    classLoader.addURL(strClassFilePath.toURL())
////      load strClassFilePath
//
//    String fileName = "file://$strClassFilePath"
//    println(fileName)
//   // classLoader.rootLoader.addURL(fileName.toURL())
//
//    classLoader.addURL(fileName.toURL())
//    //classLoader.parseClass(fileName)
//
//    //classLoader.loadedClasses << classLoader.parseClass(fileName)
//
//}

//println(classLoader.loadedClasses)
//Pattern for groovy script
//classLoader.loadClass('ObjetoBase')

//l = classLoader.loadClass('br.com.btoffoli.jogoDeTurno.basico.ObjetoBase')?.newInstance()



Integer porta = (this.args && this.args[0].isInteger()) ? this.args[0].toInteger() : 2000



println porta

import logica.GerenciadorJogos

GerenciadorJogos gerenciador = new GerenciadorJogos(porta)
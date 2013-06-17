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


import basico.ObjetoBase

l = new ObjetoBase()

println(l)





//l = ClassLoader.forName('ObjetoBase', true, classLoader).newInstance()
//println(l)
//cl.loadClass('Convite', true)
//import Convite
//println new Convite()
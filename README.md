# README #

RefGen Eclipse plug-in

(c) Copyright 2017 Rodrigo Morales Alvarado
 * Ecole Polytechinique Montreal.
 * 
 * Use and copying of this software and preparation of derivative works
 * based upon this software are permitted. Any copy of this software or
 * of any derivative work must include the above copyright notice of
 * Rodrigo Morales Alvarado, this paragraph and the one after it.
 * 
 * This software is made available AS IS, and THE AUTHOR DISCLAIMS
 * ALL WARRANTIES, EXPRESS OR IMPLIED, INCLUDING WITHOUT LIMITATION THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR
 * PURPOSE, AND NOT WITHSTANDING ANY OTHER PROVISION CONTAINED HEREIN, ANY
 * LIABILITY FOR DAMAGES RESULTING FROM THE SOFTWARE OR ITS USE IS
 * EXPRESSLY DISCLAIMED, WHETHER ARISING IN CONTRACT, TORT (INCLUDING
 * NEGLIGENCE) OR STRICT LIABILITY, EVEN IF Rodrigo Morales Alvarado IS ADVISED
 * OF THE POSSIBILITY OF SUCH DAMAGES.
 * 
 * All Rights Reserved.
 
RefGen plug-in is an Eclipse plug-in developed at [Polytechinique de Montreal](http://www.polymtl.ca) for performing automatic refactoring of software anti-patterns by [Christian Kabulo](https://github.com/Espiritous0x01) and [Rodrigo Morales](http://www.swat.polymtl.ca/rmorales/). 
It is based in "RefACO DEVELOPMENT OF A TOOL FOR AUTOMATIC CODE REFACTORING" by Juan María Frías Hidalgo (University of Malaga) for his MS dissertation.
RefGen implements "RePOR", which is an automatic refactoring approach generated at Polytechnique de Montreal by Rodrigo Morales as part of its PhD Thesis.<br>
RePOR is an automatic approach based on Partial Order reduction to automatically detect, generate  and schedule refactoring operations, reducing the search effort (measured in time and number of evaluations) required, compared to state-of the art metaheuristics like Genetic Algorithm and Ant Colony.
The detection of anti-patterns is based on [DECOR](http://ieeexplore.ieee.org/abstract/document/5196681/),  and implemented using the set of tools provided by [Ptidej tool suite](http://wiki.ptidej.net/).

 So far, RefGen supports the refactoring of five [anti-patterns](http://www.swat.polymtl.ca/rmorales/Antipatterns_definitions.html), namely Blob, Lazy Class, Long-parameter list, spaghetti code, and Speculative Generality.
 
 
 

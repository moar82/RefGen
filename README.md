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
It is based on "RefACO DEVELOPMENT OF A TOOL FOR AUTOMATIC CODE REFACTORING" by Juan María Frías Hidalgo (University of Malaga) for his MS dissertation, which uses an early version of Ant Colony Optimization developed by Rodrigo Morales.
RefGen implements "RePOR", which is an automatic refactoring approach generated at Polytechnique de Montreal by Rodrigo Morales as part of its PhD Thesis.<br>

RePOR leverage Partial Order reduction to automatically detect, generate  and schedule refactoring operations, reducing the search effort (measured in time and number of evaluations) required, compared to state-of the art metaheuristics like Genetic Algorithm and Ant Colony. A research paper on RePOR is currently on revision at [Journal of Systems and Software](https://www.journals.elsevier.com/journal-of-systems-and-software/).

The detection of anti-patterns is based on [DECOR](http://ieeexplore.ieee.org/abstract/document/5196681/),  and implemented using the set of tools provided by [Ptidej tool suite](http://wiki.ptidej.net/).

So far, RefGen supports the refactoring of five [anti-patterns](http://www.swat.polymtl.ca/rmorales/Antipatterns_definitions.html), namely Blob, Lazy Class, Long-parameter list, spaghetti code, and Speculative Generality.
 
 
# RefGen plug-in installation and tutorial
Please visit the [wiki](https://github.com/moar82/RefGen/wiki) in this repository for further details.
 
#  Research.
 
 Here is the list of our related research work about automatic refactoring of anti-patterns that I have done in Polytechnique de Montreal in collaboration with other well known researchers.
 
- Morales, R., Sabane, A., Musavi, P., Khomh, F., Chicano, F., & Antoniol, G. (2016, 14-18 March 2016). Finding the Best Compromise Between Design Quality and Testing Effort During Refactoring. Paper presented at the 2016 IEEE 23rd International Conference on Software Analysis, Evolution, and Reengineering (SANER).
[DOI](https://doi.org/10.1109/SANER.2016.23)
 
- Morales, R., Soh, Z., Khomh, F., Antoniol, G., & Chicano, F. On the use of developers’ context for automatic refactoring of software anti-patterns. Volume 128, 2017, Pages 236-251, ISSN 0164-1212, Journal of Systems and Software. 
[DOI](https://doi.org/10.1016/j.jss.2016.05.042)

- Morales, R., Chicano, F., Khomh, F. et al. Exact search-space size for the refactoring scheduling problem.  Autom Softw Eng (2017). 
 [DOI](https://doi.org/10.1007/s10515-017-0213-6)
 
- Morales, R., Saborido, R.,  Khomh, F., F., Chicano, F., & Antoniol, G, "EARMO: An Energy-Aware Refactoring Approach for Mobile Apps," in IEEE Transactions on Software Engineering, vol. PP, no. 99, pp. 1-1.
[doi](https://doi.org/10.1109/TSE.2017.2757486)


 If you plan to use RefGen in your research, please **cite at least one** of the aforementioned papers.
 
 
 
 
 

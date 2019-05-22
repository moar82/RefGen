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
 
RefGen plug-in is an Eclipse plug-in developed at [Polytechinique de Montreal](http://www.polymtl.ca) for performing automatic refactoring of software anti-patterns by [Christian Kabulo](https://github.com/cypherkhris) and [Rodrigo Morales](http://www.swat.polymtl.ca/rmorales/).  The Eclipse plug-in It is based on the MS dissertation titled "RefACO DEVELOPMENT OF A TOOL FOR AUTOMATIC CODE REFACTORING" by Juan María Frías Hidalgo (University of Malaga), which uses an early version of Ant Colony Optimization developed by Rodrigo Morales.

RefGen implements "RePOR", which is an automatic refactoring approach generated at Polytechnique de Montreal by Rodrigo Morales as part of its Ph.D. Thesis.

RePOR leverage Partial Order reduction to automatically detect, generate  and schedule refactoring operations, reducing the search effort (measured in time and number of evaluations) required, compared to state-of the art metaheuristics like Genetic Algorithm and Ant Colony. A research paper on RePOR was submitted to [Journal of Systems and Software](https://www.sciencedirect.com/science/article/pii/S0164121218301523).

The detection of anti-patterns is based on [DECOR](http://ieeexplore.ieee.org/abstract/document/5196681/),  and implemented using the set of tools provided by [Ptidej tool suite](http://wiki.ptidej.net/).

So far, RefGen supports the refactoring of five [anti-patterns](http://www.swat.polymtl.ca/rmorales/Antipatterns_definitions.html), namely Blob, Lazy Class, Long-parameter list, spaghetti code, and Speculative Generality.
 
# Why refactoring and why you should care
> By continuously improving the design of code, we make it easier and easier to work with. This is in sharp contrast to what typically happens: little refactoring and a great deal of attention paid to expediently adding new features. If you get into the hygienic habit of refactoring continuously, you'll find that it is easier to extend and maintain code.
> — Joshua Kerievsky, Refactoring to Patterns

Manual refactoring is hard due to:
* Many options to choose
* Refactoring solutions can be conflicted, making difficult to decide which refactorings apply and in which order

![img1](http://swat.polymtl.ca/rmorales/many_choices.png "Different possibilities to move a method between two classes in Android app")

For all reasons above we developed an automated approach to refactor a system, requiring a minimal interaction from developer.

# RefGen plug-in installation and tutorial
Please visit the [wiki](https://github.com/moar82/RefGen/wiki) in this repository for further details.
In addition, you can run RefGen in *simulation mode* (applying refactoring to an abstract model, without modifying your code) to test our tool. [wiki](https://github.com/moar82/RefGen/wiki/Simulation-Mode-of-RefGen).

**Do not forget to fork our repository to have the latest updates**
 
#  Research.
 
 If you want to use RefGen in your research, please **cite at least** the following reference:
 
 - Morales, R.; Chicano, F.; Khomh, F.; and Antoniol, G. Efficient Refactoring Scheduling based on Partial order Reduction.  Journal of Systems and Software. 2018
 [DOI](https://doi.org/10.1016/j.jss.2018.07.076)
 
  Here is the list of  related research work about automatic refactoring of anti-patterns that I have done in Polytechnique de Montreal in collaboration with other well known researchers.  For a complete list of works please visit my [personal web site] (https://moar82.github.io/#portfolio)
 
- Morales, R. (2015, 2-6 March 2015). Towards a Framework for Automatic Correction of Anti-Patterns. Paper presented at the 2015 IEEE 22nd International Conference on Software Analysis, Evolution, and Reengineering (SANER).
[DOI](https://doi.org/10.1109/SANER.2015.7081891)

- Morales, R., Sabane, A., Musavi, P., Khomh, F., Chicano, F., & Antoniol, G. (2016, 14-18 March 2016). Finding the Best Compromise Between Design Quality and Testing Effort During Refactoring. Paper presented at the 2016 IEEE 23rd International Conference on Software Analysis, Evolution, and Reengineering (SANER).
[DOI](https://doi.org/10.1109/SANER.2016.23)
 
- Morales, R., Soh, Z., Khomh, F., Antoniol, G., & Chicano, F. On the use of developers’ context for automatic refactoring of software anti-patterns. Volume 128, 2017, Pages 236-251, ISSN 0164-1212, Journal of Systems and Software. 
[DOI](https://doi.org/10.1016/j.jss.2016.05.042)

- Morales, R., Chicano, F., Khomh, F. et al. Exact search-space size for the refactoring scheduling problem.  Autom Softw Eng (2017). 
 [DOI](https://doi.org/10.1007/s10515-017-0213-6)
 
- Morales, R., Saborido, R.,  Khomh, F., F., Chicano, F., & Antoniol, G, "EARMO: An Energy-Aware Refactoring Approach for Mobile Apps," in IEEE Transactions on Software Engineering, vol. PP, no. 99, pp. 1-1.
[doi](https://doi.org/10.1109/TSE.2017.2757486)

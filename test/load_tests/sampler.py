#!/usr/bin/python
# Reference implementation code for pseudo-random sampler
# for election audits or other purposes.
# Written by Ronald L. Rivest
# filename: sampler.py
# url: http://people.csail.mit.edu/rivest/sampler.py
sampler_version = "November 14, 2011"
# 
# Relevant to document being produced by an ad-hoc working group chaired
# by Prof. Philip Stark (U.C. Berkeley) regarding election auditing.
# Tested using python version 2.6.7.   (see www.python.org)
# (Will not work with Python version 3, e.g. 3.x.y)
# (Note added 2014-09-07: As per a suggestion by Chris Jerdonek, one should
#  consider this proposal as based on the use of  UTF-8 encoding for strings 
#  throughout.  This comment resolves some potential ambiguities about how 
#  strings are converted to byte sequences before hashing, and the types of
#  strings input by raw_input, etc.  See
#    https://github.com/cjerdonek/rivest-sampler-tests
# for more discussion and test-cases.
# )

"""
This program provides a reference implementation of a recommended procedure 
to pick a random sample of a given size from a specified set of integers.

This program is "open source" (MIT License) and may be freely used in
almost any way whatsoever by others. (Details given below)

The specified set of integers from which the random sample is drawn 
must of the form {a, a+1, ..., b} for some integers a and b, where a
is not greater than b.  That is, the integer a is the least integer in 
the specified set, and the integer b is the greatest integer in the 
specified set.

The user may request that the sampling be done either "with replacement"
or "without replacement".  If sampling is done without replacement, then the
elements of the sample will be distinct---no repetitions will occur.
On the other hand, if sampling is done with replacement, then the
sample may contain repeated elements.  The program variable
"with_replacement" is True if sampling is to be done with replacement,
otherwise this program variable is False.

The size n of the desired sample is an input parameter.  Here n must
be a non-negative integer.

If sampling is done without replacement, then we must have that
n is not larger than b-a+1, the size of the specified set from which
the sample is drawn, otherwise no such sample exists.

The sampling is not truly random, but is "pseudo-random".  The randomness
necessary for the sampling is derived from a "seed value" that is
input to the program.  

The seed value is a text sequence entered on one of more lines by the user.  
This seed value should be generated in a truly random manner, such as
by rolling a die many times.  In some situations (such as for election
audits), it may be desirable to have different users generate different
portions of the seed, so that no one user can control the random sample
completely.  For example, there may be five different users, each of
whom rolls a normal die six times.  For example, the users may enter the
following seed values:
    seed value >>> 126331
    seed value >>> 563425
    seed value >>> 354643
    seed value >>> 662325
where user 1 rolled the die six times to obtain 1,2,6,3,3,1, user 2
rolled the die six times to obtain 5,6,3,4,2,5, and so on.  In
this case the complete seed used by the program is
    126331563425354643662325
Having at least twenty-four random characters or digits in the complete
seed value is recommended practice.  For the purposes of election audits,
the seed value should not be determined until after the election is complete
and the audit is about to begin.  Note that the seed value is the *only*
source of randomness employed by this program, so that using the same
seed value again (as well as the same n, a, b, and with_replacement values)
will produce exactly the same sample.

The program prompts for parameters n, a, b, seed, and with_replacement.

The program also prompts for an "election ID" which is an arbitrary
string of text that is used merely for documentation.

The requested sample of size n is printed twice: once in the order the
sample elements were generated, and once in sorted into numerically
increasing order.

The sample produced may also be written to a file, at the user's
request.  This output file contains documentation regarding n, a, b,
with_replacement, the election ID, and the seed value, as well as the
sample produced.

Because of the method used, the sample produced should be a "uniform"
sample.  That is, each possible sample of the desired size should
be equally likely.  If the sampling is done "without replacement"
(no duplicates allowed), then each integer in the range a...b
(inclusive) should be equally likely to appear in the sample.
The procedure used here would need to be modified if some form
of non-uniform sampling were desired.

It is also possible to have the program produce an "expanded
sample".  By giving the same parameters seed, a, and b, the
same sequence will be produced as were produced on an earlier
run.  If you wish a longer sequence to be produced than the
earlier run, the program will prompt for the length of the
earlier run, and the number of new additional elements to
be generated.  In this way, support is provided for an
"escalation" of an audit.

We now give a brief description of the method employed to 
produce the sample.

The cryptographic hash function SHA-256 is used in this program.
This hash function maps arbitrary strings of input text to
"pseudo-random" 256-bit integers.  The pseudo-randomness of this
function is of the highest quality: SHA-256 is a U.S. government
standard and has passed the most stringest testing.

The SHA-256 hash function is used in "counter mode" to obtain
the desired sample.  The sample elements are picked one by
one from a..b, with the i-th pick is generated by applying
SHA-256 to the text string obtained by following the seed
by a comma and then the decimal representation of i.  This
value reduced modulo (b-a+1) and added to a to obtain a value
in the range a..b.  This value is rejected if sampling is done
without replacement and the value obtained is a duplicate of
a previously obtained value.

We now give a sample transcript of the running of this program.

$ python sampler.py

SAMPLER -- pseudo-random sample generation for election auditing or other uses.
Written by Ronald L. Rivest.
Sampler version:  November 14, 2011
Python version:  2.6.7
Generates a sample of size n of integers from a to b (inclusive)
based on supplied parameters, including seed value(s)
and the specification as to whether sampling with replacement is desired.

Current date/time: 2011-11-14 20:28:42.477502

(1) Enter text describing election id (e.g. name and date), then hit return
    This is for documentation purposes only, and does not affect computation.
    Example: 
        Election ID >>> Gotham City Mayor's Race, November 2072

    Election ID >>> Big Apple City Council, January 20th, 2034
    ------
    Election ID = Big Apple City Council, January 20th, 2034

(2) Enter one or more lines of text giving random number seed values.
    These are typically decimal numbers, but may be arbitrary strings.
    Embedded blanks OK, but initial and trailing blanks ignored.
    Using decimal dice to generate these values is good practice.
    Having different parties generate different seed values is good practice.
    Having at least 24 random digits entered total is good practice.
    When finished, enter a blank line.
    Example: (USE NEW RANDOM VALUES, NOT THESE ONES)
        seed value (or blank line when done) >>> 314525782315
        seed value (or blank line when done) >>> 667241589410
        seed value (or blank line when done) >>> 

    seed value (or blank line when done) >>> 3546311
    seed value (or blank line when done) >>> 5561121
    seed value (or blank line when done) >>> 6362461
    seed value (or blank line when done) >>> 5351222
    seed value (or blank line when done) >>> 
    ------
    Seed = 3546311556112163624615351222

(3) Outputs will be in range  a  to  b , inclusive
    Enter integer a (start of range)
    Example:
        a >>> 1

    a >>> 1

    Enter integer b (end of range)
    Example:
        b >>> 213

    b >>> 876
    ------
    a =  1 , b =  876
    N = 876  (number of integers in set to draw sample from)

(4) Are duplicates OK (i.e. sample with replacement)?
    Example:
        Duplicates OK (sample with replacement) (y or n)? >>> n

    Duplicates OK (sample with replacement) (y or n)? >>> n
    ------
    Duplicate outputs not allowed (that is sampling 'without replacement')

(5) Are you now asking for an expanded version of a previously generated sample?
    Example:
        Is this an expanded version of a previously generated sample? >>> n

    Is this an expanded version of a previously generated sample? >>> n

    How many outputs do you want (integer n)?
    Example: 
        n >>> 43

    n >>> 47
    ------
    Request is for a new sample of size n = 47

(6) Generating output:
          1. 3546311556112163624615351222,1 ==> 740
          2. 3546311556112163624615351222,2 ==> 180
          3. 3546311556112163624615351222,3 ==> 264
          4. 3546311556112163624615351222,4 ==> 789
          5. 3546311556112163624615351222,5 ==> 238
          6. 3546311556112163624615351222,6 ==> 448
          7. 3546311556112163624615351222,7 ==> 272
          8. 3546311556112163624615351222,8 ==> 611
          9. 3546311556112163624615351222,9 ==> 761
         10. 3546311556112163624615351222,10 ==> 208
         11. 3546311556112163624615351222,11 ==> 596
         12. 3546311556112163624615351222,12 ==> 88
         13. 3546311556112163624615351222,13 ==> 160
         14. 3546311556112163624615351222,14 ==> 113
         15. 3546311556112163624615351222,15 ==> 766
         16. 3546311556112163624615351222,16 ==> 427
         17. 3546311556112163624615351222,17 ==> 184
         18. 3546311556112163624615351222,18 ==> 816
         19. 3546311556112163624615351222,19 ==> 653
         20. 3546311556112163624615351222,20 ==> 411
         21. 3546311556112163624615351222,21 ==> 779
         22. 3546311556112163624615351222,22 ==> 331
         23. 3546311556112163624615351222,23 ==> 339
         24. 3546311556112163624615351222,24 ==> 487
         25. 3546311556112163624615351222,25 ==> 594
         26. 3546311556112163624615351222,26 ==> 235
         27. 3546311556112163624615351222,27 ==> 65
         28. 3546311556112163624615351222,28 ==> 527
         29. 3546311556112163624615351222,29 ==> 821
         30. 3546311556112163624615351222,30 ==> 490
         31. 3546311556112163624615351222,31 ==> 461
         31. 3546311556112163624615351222,32 ==> 611  (duplicate rejected)
         32. 3546311556112163624615351222,33 ==> 251
         33. 3546311556112163624615351222,34 ==> 471
         34. 3546311556112163624615351222,35 ==> 414
         35. 3546311556112163624615351222,36 ==> 174
         36. 3546311556112163624615351222,37 ==> 567
         37. 3546311556112163624615351222,38 ==> 300
         38. 3546311556112163624615351222,39 ==> 134
         39. 3546311556112163624615351222,40 ==> 144
         40. 3546311556112163624615351222,41 ==> 357
         41. 3546311556112163624615351222,42 ==> 786
         42. 3546311556112163624615351222,43 ==> 792
         43. 3546311556112163624615351222,44 ==> 218
         44. 3546311556112163624615351222,45 ==> 550
         45. 3546311556112163624615351222,46 ==> 787
         46. 3546311556112163624615351222,47 ==> 537
         47. 3546311556112163624615351222,48 ==> 197

    Unsorted list of outputs:
         740      180      264      789      238      448      272      611      761      208 
         596       88      160      113      766      427      184      816      653      411 
         779      331      339      487      594      235       65      527      821      490 
         461      251      471      414      174      567      300      134      144      357 
         786      792      218      550      787      537      197 

    Sorted list of outputs:
          65       88      113      134      144      160      174      180      184      197 
         208      218      235      238      251      264      272      300      331      339 
         357      411      414      427      448      461      471      487      490      527 
         537      550      567      594      596      611      653      740      761      766 
         779      786      787      789      792      816      821 

(7) Saving results to file if desired.
    Example: 
        Name of output file (or blank line if saving results to file not desired): sample-2072-11-03.txt

    Name of output file (or blank line if saving results to file not desired): BigAppleCityCouncil.txt
    Results saved in output file: BigAppleCityCouncil.txt

Current date/time: 2011-11-14 20:33:11.967061

Done.

Here is the output file generated (filename BigAppleCityCouncil.txt):

SAMPLER output.
SAMPLER from http://people.csail.mit.edu/rivest/sampler.py
SAMPLER version: November 14, 2011
Date/Time: 2011-11-14 20:33:11.965916

Election ID: Big Apple City Council, January 20th, 2034
Sample range: a = 1 to b = 876 (inclusive)
Duplicates not allowed (sampling without replacement).
Seed: 3546311556112163624615351222
Sample of size: n = 47
Sorted output list:
         65,      88,     113,     134,     144,     160,     174,     180,     184,     197, 
        208,     218,     235,     238,     251,     264,     272,     300,     331,     339, 
        357,     411,     414,     427,     448,     461,     471,     487,     490,     527, 
        537,     550,     567,     594,     596,     611,     653,     740,     761,     766, 
        779,     786,     787,     789,     792,     816,     821, 

Done.

"""

################################################################################
## Standard "MIT License"  http://www.opensource.org/licenses/mit-license.php ##
################################################################################
"""
Copyright (c) 2011 Ronald L. Rivest

Permission is hereby granted, free of charge, to any person obtaining
a copy of this software and associated documentation files (the
"Software"), to deal in the Software without restriction, including
without limitation the rights to use, copy, modify, merge, publish,
distribute, sublicense, and/or sell copies of the Software, and to
permit persons to whom the Software is furnished to do so, subject to
the following conditions:

The above copyright notice and this permission notice shall be
included in all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE
LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION
OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION
WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
"""
################################################################################
################################################################################

# import library of cryptography hash functions
# This program uses SHA-256 hash function
# For reference, see, e.g.
#     http://en.wikipedia.org/wiki/SHA-2
#     http://csrc.nist.gov/publications/fips/fips180-2/fips180-2.pdf
# SHA-256 implemented here as hashlib.sha256
import hashlib                       

# import library of string-related functions
import string

# import library of date/time routines
import datetime

# get python version
import platform
python_version = platform.python_version()

def generate_outputs(n,with_replacement,a,b,seed,skip):
    """
    This routine returns two lists:
        a list of size 'skip' of "old output values" (i.e., the "previous sample")
        a list of size 'n-skip' new output values,
    each from the range [a..b] (inclusive).
    If 'with_replacement' is False, then no duplicates are produced.
    The input 'seed' is an arbitrary string of characters.
    The first 'skip' values produced will be skipped.
    Typically skip is 0, but if you are expanding a sample,
    then skip will be the size of the previously generated
    sample.  (The same seed etc. should be given.)
    An assertion error is raised if with_replacement is False
    and n+skip>(b-a+1) since the request is infeasible.
    The list produced is effectively a 'random sample'
    of the desired size from the universe [a..b], 
    with each element equally likely to be picked.
    """
    # check that input parameters are valid
    assert n >= 0
    assert a <= b
    N = (b - a + 1)                        # size of set to draw from 
    assert (with_replacement or n <= N)

    #initialization
    new_output_list = [ ]
    old_output_list = [ ]
    count = 0        
    # printing_wanted = True
    printing_wanted = False
    if printing_wanted:
        print "(6) Generating output:"

    # loop until we have generated the desired sample of size n
    while len(old_output_list)+len(new_output_list) < n:

        count = count + 1
        
        # hash_input is seed followed by comma followed by decimal rep of count
        hash_input = seed + "," + str(count)
        # Apply SHA-256, interpreting hex output as hexadecimal integer
        # to yield 256-bit integer (a python "long integer")
        hash_output = int(hashlib.sha256(hash_input).hexdigest(),16)
        # determine "pick" as pseudo-random value in range a to b, inclusive,
        # as a function of hash_output
        pick = int(a + (hash_output % (b-a+1)))
        
        if not with_replacement and (pick in new_output_list or pick in old_output_list):
            if printing_wanted:
                print "    %7d."%(len(new_output_list)),hash_input,"==>",pick," (duplicate rejected)"
        else:
            if len(old_output_list) < skip:
                old_output_list.append(pick)
                if printing_wanted:
                    print "    %7d."%len(new_output_list),hash_input,"==>",pick," (skipped, since it was in previous sample)"
            else:
                new_output_list.append(pick)
                if printing_wanted:
                    print "    %7d."%len(new_output_list),hash_input,"==>",pick

    return (old_output_list,new_output_list)

def print_list(L):
    """
    Print list L of integers, with at most 10 per line
    """
    for i in range(0,len(L),10):
        print "    ",
        for j in range(min(10,len(L)-i)):
            print "%7d "%L[i+j],
        print

def write_list_to_file(file,L):
    """
    similar to print_list, but writing to given file instead.
    """
    number_per_line = 10
    csv_wanted = True           # else tabs
    for i in range(0,len(L),number_per_line):
        file.write("    ")
        for j in range(min(number_per_line,len(L)-i)):
            file.write("%7d"%L[i+j])
            if csv_wanted:
                file.write(", ")
            else:
                file.write("\t")
        file.write("\n")

def main():
    print
    print "SAMPLER -- pseudo-random sample generation for election auditing or other uses."
    print "Written by Ronald L. Rivest."
    print "Sampler version: ",sampler_version
    print "Python version: ",python_version
    print "Generates a sample of size n of integers from a to b (inclusive)"
    print "based on supplied parameters, including seed value(s)"
    print "and the specification as to whether sampling with replacement is desired."
    print 
    print "Current date/time:", datetime.datetime.now().isoformat(" ")
    print

    print "(1) Enter text describing election id (e.g. name and date), then hit return"
    print "    This is for documentation purposes only, and does not affect computation."
    print "    Example: "
    print "        Election ID >>> Gotham City Mayor's Race, November 2072"
    print 
    electionid = raw_input("    Election ID >>> ")
    print "    ------"
    print "    Election ID =",str(electionid)
    print

    print "(2) Enter one or more lines of text giving random number seed values."
    print "    These are typically decimal numbers, but may be arbitrary strings."
    print "    Embedded blanks OK, but initial and trailing blanks ignored."
    print "    Using decimal dice to generate these values is good practice."
    print "    Having different parties generate different seed values is good practice."
    print "    Having at least 24 random digits entered total is good practice."
    print "    When finished, enter a blank line."
    print "    Example: (USE NEW RANDOM VALUES, NOT THESE ONES)"
    print "        seed value (or blank line when done) >>> 314525782315"
    print "        seed value (or blank line when done) >>> 667241589410"
    print "        seed value (or blank line when done) >>> "
    print 
    seedlist = [ ] 
    while True:
        seed_value = raw_input("    seed value (or blank line when done) >>> ")
        seed_value = seed_value.strip()        # eliminate initial and trailing blanks
        if seed_value == "":
            break
        seedlist.append(seed_value)
    # concatenate all seeds together
    seed = string.join(seedlist,"")
    print "    ------"
    print "    Seed =",str(seed)
    print

    print "(3) Outputs will be in range  a  to  b , inclusive"
    print "    Enter integer a (start of range)"
    print "    Example:"
    print "        a >>> 1"
    print
    a = int(raw_input("    a >>> "))
    print
    print "    Enter integer b (end of range)"
    print "    Example:"
    print "        b >>> 213"
    print
    b = int(raw_input("    b >>> "))
    assert (a<b)
    print "    ------"
    print "    a = ",a,", b = ",b
    N = (b - a + 1)
    print "    N = %d  (number of integers in set to draw sample from)"%N
    print

    print "(4) Are duplicates OK (i.e. sample with replacement)?"
    print "    Example:"
    print "        Duplicates OK (sample with replacement) (y or n)? >>> n"
    print
    with_replacement = raw_input("    Duplicates OK (sample with replacement) (y or n)? >>> ")
    with_replacement = string.strip(with_replacement)
    with_replacement = string.lower(with_replacement)
    assert (with_replacement == 'y' or with_replacement == 'n')
    with_replacement = (with_replacement == 'y')
    print "    ------"
    if with_replacement:
        print "    Duplicate outputs OK (that is, sampling 'with replacement')."
    else:
        print "    Duplicate outputs not allowed (that is sampling 'without replacement')"
    print

    print "(5) Are you now asking for an expanded version of a previously generated sample?"
    print "    Example:"
    print "        Is this an expanded version of a previously generated sample? >>> n"
    print 
    expanded_sample = raw_input("    Is this an expanded version of a previously generated sample? >>> ")
    print
    expanded_sample = string.strip(expanded_sample)
    expanded_sample = string.lower(expanded_sample)
    assert (expanded_sample == 'y' or expanded_sample == 'n')
    expanded_sample = (expanded_sample == 'y')
    if expanded_sample:
        print "    What was the size of that previous sample? "
        print "    Example: "
        print "        What was the size of the previous sample? >>> 21"
        print
        skip = raw_input("    What was the size of the previous sample? >>> ")
        skip = int(skip)
        print
        print "    How many additional output elements do you now want? "
        print "    Example: "
        print "        How many additional output elements do you now want? >>> 25"
        print
        new_elts = raw_input("    How many additional output elements do you now want? >>> ")
        new_elts = int(new_elts)
        print
        n = skip + new_elts
        assert with_replacement or (n <= N)
    else:
        print "    How many outputs do you want (integer n)?"
        print "    Example: "
        print "        n >>> 43"
        print
        n = int(raw_input("    n >>> "))   
        assert (n>0)
        assert with_replacement or (n <= N)
        skip = 0
        new_elts = n

    print "    ------"
    if expanded_sample:
        print "    Request is for an expanded sample."
        print "    Size of previous sample (number of elements to skip now) is %d"%skip
        print "    Number of new elements to generate is %d"%new_elts
        print
    else:
        print "    Request is for a new sample of size n = %d"%n
        print

    old_output_list,new_output_list = generate_outputs(n,with_replacement,a,b,seed,skip)
                
    print

    if len(old_output_list)>0:
        print "    Unsorted list of outputs in previous sample:"
        print_list(old_output_list)
        print 
        print "    Sorted list of outputs in previous sample:"
        sorted_old_output_list = sorted(old_output_list)
        print_list(sorted_old_output_list)
        print
        print "    Unsorted list of new outputs:"
        print_list(new_output_list)
        print 
        print "    Sorted list of new outputs:"
        sorted_new_output_list = sorted(new_output_list)
        print_list(sorted_new_output_list)
        print
    else:
        print "    Unsorted list of outputs:"
        print_list(new_output_list)
        print 
        print "    Sorted list of outputs:"
        sorted_new_output_list = sorted(new_output_list)
        print_list(sorted_new_output_list)
        print

    # Write output to a file as well
    print "(7) Saving results to file if desired."
    print "    Example: "
    print "        Name of output file (or blank line if saving results to file not desired): sample-2072-11-03.txt"
    print
    filename = raw_input("    Name of output file (or blank line if saving results to file not desired): ")
    
    if filename != "":
        file = open(filename,"w")
        file.write("SAMPLER output.\n")
        file.write("SAMPLER from http://people.csail.mit.edu/rivest/sampler.py\n")
        file.write("SAMPLER version: "+sampler_version+"\n")
        file.write("Date/Time: "+datetime.datetime.now().isoformat(" ")+"\n\n")
        file.write("Election ID: "+electionid+"\n")
        file.write("Sample range: a = %d to b = %d (inclusive)\n"%(a,b))
        if with_replacement:
            file.write("Duplicates allowed (sampling with replacement).\n")
        else:
            file.write("Duplicates not allowed (sampling without replacement).\n")
        file.write("Seed: "+seed+"\n")
        if skip == 0:
            file.write("Sample of size: n = %d\n"%n)
            file.write("Sorted output list:\n")
            write_list_to_file(file,sorted_new_output_list)
            file.write("\n")
        else:
            file.write("Previous sample of size %d\n"%skip)
            write_list_to_file(file,sorted_old_output_list)
            file.write("\n")
            file.write("New elements in expanded sample (%d of them)\n"%new_elts)
            write_list_to_file(file,sorted_new_output_list)
            file.write("\n")
        file.write("Done.\n")
        file.close()
        print "    Results saved in output file:",filename
        print
    else:
        print "    No output file written."
        print
        
    print "Current date/time:", datetime.datetime.now().isoformat(" ")
    print
    print "Done."

if __name__ == "__main__":
    main()

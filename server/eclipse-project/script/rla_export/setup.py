#!/usr/bin/env python

from setuptools import setup

setup(
    setup_requires=['pbr'],
    pbr=True,
)

"""
from setuptools import setup, find_packages


def readme():
    with open('README.rst') as f:
        return f.read()


setup(name='rla_export',
      version='0.8',
      description='Export data for publication and audit verification from ColoradoRLA: Software to facilitate risk-limiting post-election tabulation audits',
      long_description=readme(),
      url='https://github.com/FreeAndFair/ColoradoRLA',
      author='Neal McBurnett',
      author_email='nealmcb@freeandfair.us',
      license='GPLv3+',
      classifiers=[
        'Development Status :: 3 - Alpha',
        'License :: OSI Approved :: GNU General Public License v3 or later (GPLv3+)',
        'Programming Language :: Python :: 2.7',
        'Programming Language :: SQL',
        'Intended Audience :: Other Audience',
        'Intended Audience :: Science/Research',
        # Need more "Intended Audience" options, like:
        #  Government
        #  Election Administrators
        'Topic :: Other/Nonlisted Topic',
        'Topic :: Scientific/Engineering :: Human Machine Interfaces',
        'Topic :: Scientific/Engineering :: Information Analysis',
        'Topic :: Security',
        # Need more "Topic" options, like:
        #  Politics
        #  Government
        #  Auditing
        #  Elections
      ],
      keywords='audit election psycopg2 csv json sql',
      # Note: this project is one part of the ColoradoRLA repo
      packages=find_packages(),
      # python_requires='==2.7',
      install_requires=[
          'requests>=2.4.2',
          'psycopg2',
          'setuptools-git',
      ],
      # test_suite='nose.collector',
      # tests_require=['nose', 'nose-cover3'],
      entry_points={
          'console_scripts': ['rla_export = rla_export.__main__:main'],
      },
      include_package_data=True,
      # TODO: include external file ColoradoRLA/server/eclipse-project/src/main/resources/us/freeandfair/corla/default.properties rather than a copy of it
      zip_safe=False)
"""

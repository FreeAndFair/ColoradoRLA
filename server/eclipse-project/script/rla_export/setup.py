from setuptools import setup, find_packages


def readme():
    with open('README.rst') as f:
        return f.read()


setup(name='rla_export',
      version='1.1.0.3',
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
      keywords='audit election psycopg2',
      # Note: this project is one part of the ColoradoRLA repo
      packages=['rla_export'],
      data_files=[('rla_export', ['rla_export/corla.ini'])],  # Why is default.properties getting in? add it here also?
      # python_requires='==2.7',
      install_requires=[
          'requests>=2.12.1',
          'psycopg2>=2.7.3.1',
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

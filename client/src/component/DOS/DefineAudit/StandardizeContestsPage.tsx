import * as React from 'react';

import * as _ from 'lodash';

import { MenuItem } from '@blueprintjs/core';
import { Select } from '@blueprintjs/labs';

import Nav from '../Nav';

import counties from 'corla/data/counties';

export const Breadcrumb = () => (
    <ul className='pt-breadcrumbs'>
        <li>
            <a className='pt-breadcrumb' href='/sos'>SoS</a>
        </li>
        <li>
            <a className='pt-breadcrumb' href='/sos/audit'>Audit Admin</a>
        </li>
        <li>
            <a className='pt-breadcrumb pt-breadcrumb-current'>Standardize Contest Names</a>
        </li>
    </ul>
);

interface PageProps {
  forms: DOS.Form.StandardizeContests.Ref;
  canonicalContests: DOS.CanonicalContests;
  contests: DOS.Contests;
  back: OnClick;
  nextPage: OnClick;
}

export const StandardizeContestsPage = (props: PageProps) => {
    const { canonicalContests, contests, forms, back, nextPage } = props;

    // XXX: || {} is to appease the type checker
    return (
      <div>
          <Nav />
          <Breadcrumb />

          <h2>Standardize Contest Names</h2>

          <p>
              Contest names must be standardized to group records correctly across
              jurisdictions. Below is a list of contests that do not match the
              standardized contest names provided by the state. For each of the
              contests listed, please choose the appropriate standardized version
              from the options provided, then save your choices and move forward.
          </p>

          <div className='pt-card'>
              <StandardizeContestsForm formData={ forms.standardizeContestsForm || {}}
                                       canonicalContests={ canonicalContests }
                                       contests={ contests } />
          </div>

          <div>
              <button onClick={ back } className='pt-button pt-breadcrumb'>
                  Back
              </button>
              <button onClick={ nextPage } className='pt-button pt-intent-primary pt-breadcrumb'>
                  Save & Next
              </button>
          </div>
      </div>
    );
};

interface StandardizeContestsFormProps {
    contests: DOS.Contests;
    canonicalContests: DOS.CanonicalContests;
    formData: DOS.Form.StandardizeContests.FormData;
}

const StandardizeContestsForm = (props: StandardizeContestsFormProps) => {
    const { canonicalContests, contests, formData } = props;

    return (
        <table className='pt-table pt-striped'>
            <thead>
                <tr>
                    <th>County</th>
                    <th>Current Contest Name</th>
                    <th>Standardized Contest Name</th>
                </tr>
            </thead>
            <ContestBody contests={ contests }
                         canonicalContests={ canonicalContests }
                         formData={ formData } />
        </table>
    );
};

interface ContestBodyProps {
    contests: DOS.Contests;
    canonicalContests: DOS.CanonicalContests;
    formData: DOS.Form.StandardizeContests.FormData;
}

const ContestBody = (props: ContestBodyProps) => {
    const { canonicalContests, contests, formData } = props;

    const rows = _.map(contests, c => {
        return <ContestRow key={ c.id }
                           contest={ c }
                           canonicalContests={ canonicalContests }
                           formData={ formData } />;
    });

    return (
      <tbody>{ rows }</tbody>
    );
};

interface ContestRowProps {
    contest: Contest;
    canonicalContests: DOS.CanonicalContests;
    formData: DOS.Form.StandardizeContests.FormData;
}

const ContestRow = (props: ContestRowProps) => {
    const { canonicalContests, contest, formData } = props;
    const countyName = counties[contest.countyId].name;

    let standards: string[] = [];
    if (!_.isEmpty(canonicalContests[countyName])) {
        standards = _.clone(canonicalContests[countyName]);
    }

    const changeHandler = (e: any) => {
        const v = String(e.target.value);

        if (_.isEmpty(v)) {
            delete formData[contest.id];
        } else {
            formData[contest.id] = {name: v};
        }
    };

    return (
        <tr>
            <td>{ counties[contest.countyId].name }</td>
            <td>{ contest.name }</td>
            <td>
                <form>
                    <select name={ String(contest.id) }
                            onChange={ changeHandler }>
                        <option value=''>-- No change --</option>
                        {
                          _.map(standards, n => {
                              return <option value={ n }>{ n }</option>;
                          })
                        }
                    </select>
                </form>
            </td>
        </tr>
    );
};

import * as React from 'react';


const TempUserLoginPage = ({ loginAsCounty, loginAsSoS }: any) => (
    <div className='pt-card'>
        <h2>Select User Type</h2>
        <div className='pt-card'>
            This <strong> temporary </strong> page allows you to test the two main dashboards
            of the CORLA system by logging in as a specific kind of user. It will not appear in
            the final system, as user roles will be automatically detected and enforced based upon
            login credentials. This page only exists as a convenience for testing.
        </div>
        <div className='pt-card'>
            Please select what kind of user to log in as:
        </div>
        <div className='pt-card'>
            <button onClick={ loginAsSoS } className='pt-button pt-intent-primary'>
                Department of State Admin
            </button>
        </div>
        <div className='pt-card'>
            <button onClick={ loginAsCounty } className='pt-button pt-intent-primary'>
                County Official
            </button>
        </div>
    </div>
);


export default TempUserLoginPage;

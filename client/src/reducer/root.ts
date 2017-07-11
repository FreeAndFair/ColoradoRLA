const defaultState = { greeting: 'Hello' };

export default function root(state: any = defaultState, action: any) {
  switch (action.type) {

  case 'NEXT_GREETING':
      const greeting
          = state.greeting == 'Hello'
          ? 'Hi'
          : 'Hello';
      return { greeting };

  default:
      return state;
  }
}

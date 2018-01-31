class FlightInviteEditor extends React.Component {

  render() {
    return (
      <div className="editor-page">
        <form>
          <fieldset>
            <fieldset className="form-group">
              <input
                className="form-control"
                type="number"
                placeholder="Seats"
              />
            </fieldset>
          </fieldset>
        </form>
      </div>);
  }
}
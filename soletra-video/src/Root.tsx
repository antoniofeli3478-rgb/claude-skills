import {Composition} from 'remotion';
import {Soletra} from './Soletra';
import tl from './timeline.json';

export const Root: React.FC = () => (
	<Composition id="Soletra" component={Soletra} durationInFrames={tl.total}
		width={1080} height={1920} fps={30} defaultProps={{}} />
);
